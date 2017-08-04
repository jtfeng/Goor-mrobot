package cn.muye.area.map.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Jelynn on 2017/7/7.
 */
@Service
public class MapSyncService implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSyncService.class);
    private static ApplicationContext applicationContext;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotService robotService;

    @Autowired
    private MapZipService mapZipService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private OffLineMessageService offLineMessageService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    public String syncMap(long storeId) {
        MapZip mapZip = mapZipService.latestZip(storeId);
        return syncMap(mapZip, storeId);
    }

    public String syncMap(MapZip mapZip, long storeId) {
        List<Robot> robotList = robotService.listRobot(storeId);
        return syncMap(mapZip, robotList);
    }

    public String syncMap(MapZip mapZip, List<Robot> robotList) {
        //TODO 向机器人发送消息，更新地图
        return sendMapSyncMessage(robotList, mapZip);
    }

    /**
     * 同步地图
     * @param robotList
     * @param mapZip
     * @return
     */
    public String sendMapSyncMessage(List<Robot> robotList, MapZip mapZip) {
        try {
            if (null == applicationContext) {
                LOGGER.error("applicationContext 为空");
                return "";
            }
            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            if (null == rabbitTemplate) {
                LOGGER.error("rabbitTemplate 为空");
                return "";
            }
            //封装文件下载数据
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setLocalFileName(mapZip.getFileName());
            commonInfo.setLocalPath(mapZip.getRobotPath());
            commonInfo.setRemoteFileUrl(DOWNLOAD_HTTP + mapZip.getFilePath());
            commonInfo.setMD5(FileValidCreateUtil.fileMD5(DOWNLOAD_HOME + mapZip.getFilePath()));

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessageText(JSON.toJSONString(commonInfo));
            messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
            messageInfo.setSendTime(new Date());
            messageInfo.setSenderId("goor-server");
            messageInfo.setMessageType(MessageType.EXECUTOR_MAP);
            Map<String, AjaxResult> resultMap = new HashMap<>();
            for (int i = 0; i < robotList.size(); i++) {

                String code = robotList.get(i).getCode();
                //如果需要同步的机器为地图上传机器，则跳过
                if(code.equals(mapZip.getDeviceId()))
                    continue;
                String backResultClientRoutingKey = RabbitMqBean.getRoutingKey(code, true, MessageType.EXECUTOR_MAP.name());
                AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, messageInfo);
                if (null != ajaxClientResult) {
                    //推送成功，更新场景的状态
                    if(ajaxClientResult.getCode() == AjaxResult.CODE_SUCCESS){
                        String mapZipSceneName = mapZip.getSceneName();
                        String[]sceneNames = mapZipSceneName.split(",");
                        for(int j =0; j < sceneNames.length; j ++ ){
                            String sceneName  = sceneNames[j];
                            if(StringUtil.isNullOrEmpty(sceneName))
                                continue;
                            sceneService.checkSceneIsNeedToBeUpdated(sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID + "", Scene.SCENE_STATE.UPLOAD_SUCCESS);
                        }
                    }
                    resultMap.put(code, ajaxClientResult);
                } else {
                    resultMap.put(code, AjaxResult.failed("未获取到返回结果"));
                }
                //保存发送的信息
                messageInfo.setReceiverId(code);
                try {
                    this.messageSave(messageInfo);
                } catch (Exception e) {
                    LOGGER.error("save message error", e);
                }
            }
            if (!resultMap.isEmpty()) {
                return JSON.toJSONString(resultMap);
            }
        } catch (Exception e) {
            LOGGER.error("发送地图更新信息失败", e);
        }
        return "";
    }

    private boolean messageSave(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            return false;
        }
        OffLineMessage message = new OffLineMessage(messageInfo);
        offLineMessageService.save(message);//更新发送的消息
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapSyncService.applicationContext = applicationContext;
    }
}
