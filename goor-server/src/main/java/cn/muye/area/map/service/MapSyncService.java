package cn.muye.area.map.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.log.base.service.LogInfoXREFService;
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

    @Autowired
    private LogInfoXREFService logInfoXREFService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    public Map<String, AjaxResult> syncMap(MapZip mapZip, long storeId) {
        List<Robot> robotList = robotService.listRobot(storeId);
        return syncMap(mapZip, robotList);
    }

    public Map<String, AjaxResult> syncMap(MapZip mapZip, List<Robot> robotList) {
        //TODO 向机器人发送消息，更新地图
        return sendMapSyncMessage(robotList, mapZip);
    }

    /**
     * 同步地图
     *
     * @param robotList
     * @param mapZip
     * @return
     */
    public Map<String, AjaxResult> sendMapSyncMessage(List<Robot> robotList, MapZip mapZip) {
        return sendMapSyncMessage(robotList, mapZip, 0L);
    }

    public Map<String, AjaxResult> sendMapSyncMessage(List<Robot> robotList, MapZip mapZip, Long sceneId) {
        try {
            if (robotList.size() == 1) {
                Robot robot = robotList.get(0);
                //如果需要同步地图的机器人是上传地图的机器人，则直接更新场景的状态
                if (robot.getCode().equals(mapZip.getDeviceId())) {
                    sceneService.checkSceneIsNeedToBeUpdated(mapZip.getSceneName(), SearchConstants.FAKE_MERCHANT_STORE_ID + "", Scene.SCENE_STATE.UPLOAD_SUCCESS, sceneId);
                    return null;
                }
            }
            if (null == applicationContext) {
                LOGGER.error("applicationContext 为空");
                return null;
            }
            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            if (null == rabbitTemplate) {
                LOGGER.error("rabbitTemplate 为空");
                return null;
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
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < robotList.size(); i++) {

                Robot robot = robotList.get(i);
                if (robot == null)
                    continue;
                String code = robot.getCode();
                //如果需要同步的机器为地图上传机器，则跳过
                if (code.equals(mapZip.getDeviceId())) {
                    LOGGER.info("需同步的机器人code为上传地图的机器人code，不进行同步，code=" + code);
                    continue;
                }

                String backResultClientRoutingKey = RabbitMqBean.getRoutingKey(code, true, MessageType.EXECUTOR_MAP.name());
                AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, messageInfo);
                if (null != ajaxClientResult && ajaxClientResult.getCode() == AjaxResult.CODE_SUCCESS) {
                    resultMap.put(code, ajaxClientResult);
                    stringBuffer.append(code).append(":").append("同步成功").append(",");
                } else {
                    resultMap.put(code, AjaxResult.failed("未获取到返回结果"));
                    stringBuffer.append(code).append(":").append("未获取到返回结果").append(",");
                }
            }

            //更新指定场景的state
            sceneService.checkSceneIsNeedToBeUpdated(mapZip.getSceneName(), SearchConstants.FAKE_MERCHANT_STORE_ID + "", Scene.SCENE_STATE.UPLOAD_SUCCESS, sceneId);
            Long logInfoId = LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            //保存场景操作和日志的关联关系
            logInfoXREFService.save(ModuleEnums.SCENE, sceneId, logInfoId);
            return resultMap;
        } catch (Exception e) {
            LOGGER.error("发送地图更新信息失败", e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapSyncService.applicationContext = applicationContext;
    }
}
