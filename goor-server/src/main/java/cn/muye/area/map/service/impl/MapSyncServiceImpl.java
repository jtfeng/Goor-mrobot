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
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.mrobot.utils.ZipUtils;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.MapZipService;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.base.LogInfoUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
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
    private SceneService sceneService;

    @Autowired
    private MapZipService mapZipService;

    @Autowired
    private RobotMapZipXREFService robotMapZipXREFService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    //保存地图同步的结果，地图同步操作完成后统一更新数据路
    private Map<Long, List<RobotMapZipXREF>> mapSyncResultMap;

    @Override
    public Object sendMapSyncMessageNew(List<Robot> robotList, String mapSceneName, Long sceneId) {
        LOGGER.info("开始同步地图,robotList.size()=" + robotList.size() + ",mapSceneName=" + mapSceneName);
        String mapSceneNameDir = DOWNLOAD_HOME + File.separator + SearchConstants.FAKE_MERCHANT_STORE_ID +
                File.separator + Constant.FILE_UPLOAD_TYPE_MAP + File.separator + mapSceneName;
        if (!checkApplicationContextAndRabbitTemplate()) {
            LOGGER.error("ApplicationContext And RabbitTemplate 参数为null");
            return null;
        }
        File mapSceneNameDirFile = new File(mapSceneNameDir);
        File zipFile = null;
        if (!mapSceneNameDirFile.exists() ||
                !mapSceneNameDirFile.isDirectory() ||
                mapSceneNameDirFile.listFiles().length <= 0) {
            LOGGER.error("发送地图更新信息失败，该地图场景文件夹不存在 " + mapSceneName);
            updateSceneState(Constant.UPLOAD_FAIL, sceneId);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_FSDTGXXXSBGDTCJWJJBCZ") + mapSceneName);
        }
        try {
            zipFile = zipMapFile(mapSceneNameDir, mapSceneName);
            if (null == zipFile) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_YSDTCC"));
            }
            MapZip mapZip = saveMapZip(zipFile, mapSceneName);
            return mapSync(mapZip, robotList, sceneId);
        } catch (Exception e) {
            LOGGER.error("地图同步失败 " + mapSceneName);
            updateSceneState(Constant.UPLOAD_FAIL, sceneId);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_DTTBSB"));
        }
    }

    private MapZip saveMapZip(File zipFile, String mapSceneName) {
        MapZip mapZip = new MapZip();
        mapZip.setSceneName(mapSceneName);
        mapZip.setCreateTime(new Date());
        mapZip.setDeviceId(Constant.GOOR_SERVER);
        mapZip.setFileName(zipFile.getName());
        String absolutePath = zipFile.getAbsolutePath();
        String filePath = absolutePath.replace(DOWNLOAD_HOME, "");
        mapZip.setFilePath(filePath);
        mapZip.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapZipService.save(mapZip);
        return mapZip;
    }

    private Map<String, AjaxResult> mapSync(MapZip mapZip, List<Robot> robotList, Long sceneId) throws Exception {
        MessageInfo messageInfo = getMessageInfo(mapZip);
        Map<String, AjaxResult> resultMap = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_YSBMC")).append(mapZip.getFileName()).append(",");
        int successCount = 0;
        mapSyncResultMap = new HashMap<>();
        for (int i = 0; i < robotList.size(); i++) {
            Robot robot = robotList.get(i);
            String code = robot.getCode();
            LOGGER.info("同步机器人地图，code=" + code);
            AjaxResult ajaxClientResult = sendRabbitMQMessage(code, messageInfo);
            //处理地图同步的结果
            boolean isSuccess = handleSendResult(robot, mapZip.getId(), stringBuffer, ajaxClientResult, resultMap, sceneId);
            if (isSuccess) {
                successCount++;
            }
        }
        //更新指定场景的state
        int state = successCount > 0 ? Constant.UPLOAD_SUCCESS : Constant.UPLOAD_FAIL;
        updateSceneState(state, sceneId);
        //更新地图同步结果map
        saveOrUpdateMapSyncResultMap(sceneId);
        LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
        return resultMap;
    }

    private File zipMapFile(String filePath, String mapSceneName) throws Exception {
        String zipFileSavePath = DOWNLOAD_HOME + File.separator +
                SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + Constant.MAP_SYNCED_FILE_PATH;
        String ZipFileName = mapSceneName + "_" + DateTimeUtils.getNormalNameDateTime() + Constant.ZIP_FILE_SUFFIX;
        //新建一个名称为该场景名的临时文件夹，将场景的信息拷贝到新目录，以保持压缩包目录 层级一致
        String tempDirPath = zipFileSavePath + File.separator + mapSceneName;
        File tempDir = new File(tempDirPath);
        FileUtils.deleteDirectory(tempDir);

        FileUtils.copyDirectoryToDirectory(new File(filePath), tempDir);
        //压缩临时文件夹
        boolean zipResult = ZipUtils.zip(tempDirPath, zipFileSavePath, ZipFileName);
        if (zipResult) {
            return new File(zipFileSavePath + File.separator + ZipFileName);
        }
        return null;
    }

    private void updateSceneState(int state, Long sceneId) {
        try {
            sceneService.updateSceneState(state, sceneId);
        } catch (Exception e) {
            LOGGER.error("发送地图更新信息失败", e);
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
        String httpURL = DOWNLOAD_HTTP + mapZip.getFilePath().replaceAll("\\\\", "/");
        LOGGER.info("压缩包http地址 = " + httpURL);
        commonInfo.setRemoteFileUrl(httpURL);
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
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_JQR") + robotCode + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_BZXWTBDT"));
        }
        String backResultClientRoutingKey = RabbitMqBean.getRoutingKey(robotCode, true, MessageType.EXECUTOR_MAP.name());
        AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, messageInfo);
        return ajaxClientResult;
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
                                     Map<String, AjaxResult> resultMap,
                                     Long sceneId) {
        String robotCode = robot.getCode();
        Long robotId = robot.getId();
        boolean result = false;
        if (null != ajaxClientResult && ajaxClientResult.getCode() == AjaxResult.CODE_SUCCESS) {
            resultMap.put(robotCode, ajaxClientResult);
            stringBuffer.append(robotCode).append(":").append(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_TBCG")).append(",");
            LOGGER.info("机器人" + robotCode + "同步成功");
            result = true;
        } else {
            resultMap.put(robotCode, AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_WHQDFHJG")));
            stringBuffer.append(robotCode).append(":").append(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapSyncServiceImpl_java_WHQDFHJG")).append(",");
            LOGGER.info("机器人" + robotCode + "同步失败或未获取到返回结果");
        }
        RobotMapZipXREF robotMapZipXREF = new RobotMapZipXREF.Builder().robotId(robotId).mapZipId(mapZipId).sceneId(sceneId).success(result).updateTime(new Date()).build();
        putXREFToMap(robotMapZipXREF);
        return result;
    }

    /**
     * 将地图同步结果放在map中
     * @param robotMapZipXREF
     */
    private void putXREFToMap(RobotMapZipXREF robotMapZipXREF) {
        Long sceneId = robotMapZipXREF.getSceneId();
        List<RobotMapZipXREF> robotMapZipXREFList = mapSyncResultMap.get(sceneId);
        if (null == robotMapZipXREFList){
            robotMapZipXREFList = Lists.newArrayList();
        }
        robotMapZipXREFList.add(robotMapZipXREF);
        mapSyncResultMap.put(sceneId, robotMapZipXREFList);
    }

    private void saveOrUpdateMapSyncResultMap(Long sceneId) {
        List<RobotMapZipXREF> robotMapZipXREFList = mapSyncResultMap.get(sceneId);
        //将此场景ID之前的数据置为已删除
        robotMapZipXREFService.removeBySceneId(sceneId);
        //存库
        if (null != robotMapZipXREFList && robotMapZipXREFList.size() > 0){
            for (RobotMapZipXREF robotMapZipXREF : robotMapZipXREFList){
                robotMapZipXREFService.save(robotMapZipXREF);
            }
        }
        //删除
        mapSyncResultMap.remove(sceneId);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapSyncServiceImpl.applicationContext = applicationContext;
    }
}
