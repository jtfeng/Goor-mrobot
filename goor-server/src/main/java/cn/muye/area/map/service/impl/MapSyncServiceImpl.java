package cn.muye.area.map.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.base.LogInfoUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Jelynn on 2017/7/7.
 */
@Service
public class MapSyncServiceImpl implements MapSyncService, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSyncServiceImpl.class);
    private static ApplicationContext applicationContext;
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotService robotService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private RobotMapZipXREFService robotMapZipXREFService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @Override
    public Object syncMap(MapZip mapZip, long storeId) {
        List<Robot> robotList = robotService.listRobot(storeId);
        return syncMap(mapZip, robotList);
    }

    @Override
    public Object syncMap(MapZip mapZip, List<Robot> robotList) {
        //TODO 向机器人发送消息，更新地图
        return sendMapSyncMessage(robotList, mapZip);
    }

    @Override
    public Object sendMapSyncMessage(List<Robot> robotList, MapZip mapZip) {
        return sendMapSyncMessage(robotList, mapZip, 0L);
    }

    @Override
    public Object sendMapSyncMessage(List<Robot> robotList, MapZip mapZip, Long sceneId) {
        try {
            LOGGER.info("开始同步地图,robotList.size()=" + robotList.size() + ",sceneId=" + sceneId + ",mapZip.getFileName()=" + mapZip.getFileName());
            Long mapZipId = mapZip.getId();
            if (!checkApplicationContextAndRabbitTemplate()) {
                LOGGER.error("ApplicationContext And RabbitTemplate 参数为null");
                return null;
            }
            File mapZipfile= new File(DOWNLOAD_HOME + mapZip.getFilePath());
            if (!mapZipfile.exists()){
                LOGGER.error("发送地图更新信息失败，未找到压缩包，"+ mapZipfile.getAbsolutePath());
                try {
                    sceneService.updateSceneState(Constant.UPLOAD_FAIL, sceneId);
                    return AjaxResult.failed("发送地图更新信息失败，未找到压缩包，"+ mapZipfile.getAbsolutePath());
                } catch (Exception e1) {
                    LOGGER.error("发送地图更新信息失败", e1);
                }
            }
            MessageInfo messageInfo = getMessageInfo(mapZip);
            Map<String, AjaxResult> resultMap = new HashMap<>();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("压缩包名称：").append(mapZip.getFileName()).append(",");
            int successCount = 0;
            for (int i = 0; i < robotList.size(); i++) {
                Robot robot = robotList.get(i);
                String code = robot.getCode();
                //如果需要同步的机器为地图上传机器，则跳过
                if (checkRobotIsMapUploadDevice(robot, mapZip)) {
                    successCount ++;
                    stringBuffer.append(code).append(":").append("地图上传机器人").append(",");
                    LOGGER.info("需同步的机器人code为上传地图的机器人code，不进行同步，code=" + code);
                    continue;
                }
                AjaxResult ajaxClientResult = sendRabbitMQMessage(code, messageInfo);
                //保存关联关系
                boolean isSuccess = handleSendResult(robot, mapZipId, stringBuffer, ajaxClientResult, resultMap);
                if (isSuccess){
                    successCount ++;
                }
            }
            //更新指定场景的state
            if (successCount > 0) {
                sceneService.updateSceneState(Constant.UPLOAD_SUCCESS, sceneId);
            } else {
                sceneService.updateSceneState(Constant.UPLOAD_FAIL, sceneId);
            }
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return resultMap;
        }catch (Exception e) {
            LOGGER.error("发送地图更新信息失败", e);
            try {
                sceneService.updateSceneState(Constant.UPLOAD_FAIL, sceneId);
            } catch (Exception e1) {
                LOGGER.error("发送地图更新信息失败", e);
            }
        }
        return null;
    }

    @Override
    public Object sendMapSyncMessageIgnoreUploadRobot(List<Robot> robotList, MapZip mapZip, Long sceneId) {
        return null;
    }

    private boolean checkRobotIsMapUploadDevice(Robot robot, MapZip mapZip) throws Exception {
        //如果需要同步地图的机器人是上传地图的机器人，则直接更新场景的状态
        if (robot.getCode().equals(mapZip.getDeviceId())) {
            saveOrUpdateMapZipXREF(mapZip.getId(), true, robot.getId());
            return true;
        } else {
            return false;
        }
    }

    private boolean checkApplicationContextAndRabbitTemplate() {
        if (null == applicationContext) {
            LOGGER.error("applicationContext 为空");
            return false;
        }
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        if (null == rabbitTemplate) {
            LOGGER.error("rabbitTemplate 为空");
            return false;
        }
        return true;
    }

    private MessageInfo getMessageInfo(MapZip mapZip) throws IOException {
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
        return messageInfo;
    }

    private AjaxResult sendRabbitMQMessage(String robotCode, MessageInfo messageInfo) {
        Boolean isOnline = CacheInfoManager.getRobotOnlineCache(robotCode);
        if (isOnline == null || !isOnline) {
            LOGGER.info("机器人 +" + robotCode + "不在线，未同步地图");
            return AjaxResult.failed();
        }
        String backResultClientRoutingKey = RabbitMqBean.getRoutingKey(robotCode, true, MessageType.EXECUTOR_MAP.name());
        AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, messageInfo);
        return ajaxClientResult;
    }

    private void saveOrUpdateMapZipXREF(Long newMapZipId, boolean result, Long robotId) {
        RobotMapZipXREF robotMapZipXREF = new RobotMapZipXREF.Builder().newMapZipId(newMapZipId).success(result).robotId(robotId).build();
        robotMapZipXREFService.saveOrUpdate(robotMapZipXREF);
    }

    /**
     * 返回同步操作是否成功
     *
     * @param robot
     * @param mapZipId
     * @param stringBuffer
     * @param ajaxClientResult
     * @param resultMap
     * @return
     */
    private boolean handleSendResult(Robot robot,
                                 Long mapZipId,
                                 StringBuffer stringBuffer,
                                 AjaxResult ajaxClientResult,
                                 Map<String, AjaxResult> resultMap) {
        String robotCode = robot.getCode();
        Long robotId = robot.getId();
        if (null != ajaxClientResult && ajaxClientResult.getCode() == AjaxResult.CODE_SUCCESS) {
            resultMap.put(robotCode, ajaxClientResult);
            saveOrUpdateMapZipXREF(mapZipId, true, robotId);
            stringBuffer.append(robotCode).append(":").append("同步成功").append(",");
            LOGGER.info("机器人" + robotCode + "同步成功");
            return true;
        } else {
            resultMap.put(robotCode, AjaxResult.failed("未获取到返回结果"));
            saveOrUpdateMapZipXREF(mapZipId, false, robotId);
            stringBuffer.append(robotCode).append(":").append("未获取到返回结果").append(",");
            LOGGER.info("机器人" + robotCode + "同步失败或未获取到返回结果");
            return false;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapSyncServiceImpl.applicationContext = applicationContext;
    }
}
