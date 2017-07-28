package cn.muye.log.base;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.bean.StateDetail;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.base.service.LogInfoService;
import cn.muye.log.state.StateCollectorService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jelynn on 2017/7/24.
 */
@Service
public class LogCollectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCollectService.class);
    @Autowired
    private RobotService robotService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private LogInfoService logInfoService;

    @Autowired
    private StateCollectorService stateCollectorService;

    public void startCollectLog() {
        List<Robot> robotList = robotService.listRobot(SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (robotList == null || robotList.size() <= 0) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(robotList.size());
        for (int i = 0; i < robotList.size(); i++) {
            Robot robot = robotList.get(i);
            String code = robot.getCode();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    collectChargeLog(robot);
                    collectBaseLog(code);
                    collectNavigationLog(code);
                    collectTaskLog(code);
                }
            });
            executorService.execute(thread);
        }
        executorService.shutdown();
    }


    /**
     * 收集记录电量日志
     */
    private void collectChargeLog(Robot robot) {
        String code = robot.getCode();
        ChargeInfo chargeInfo = CacheInfoManager.getRobotChargeInfoCache(code);
        if (chargeInfo == null) {
            return;
        }
        saveLogInfo(code, ModuleEnums.CHARGE, LogLevel.INFO, LogType.INFO_CHARGE, getChargeMessage(chargeInfo));

        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        int batteryThreshold = robotConfig.getBatteryThreshold();
        //保存低电量警告
        int powerPercent = chargeInfo.getPowerPercent();
        if (powerPercent <= batteryThreshold) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("电量阈值：").append(batteryThreshold).append("%,当前电量：").append(powerPercent).append("%");
            saveLogInfo(code, ModuleEnums.CHARGE, LogLevel.WARNING, LogType.WARNING_LOWER_POWER, stringBuffer.toString());
        }
    }

    /**
     * 收集记录底盘日志,只记录触发状态
     */
    private void collectBaseLog(String code){
        try{
            List<StateDetail> stateDetailList = stateCollectorService.getCurrentBaseState(code);
            if(stateDetailList != null && stateDetailList.size() > 0){
                StringBuffer stringBuffer = new StringBuffer();
                for(StateDetail stateDetail : stateDetailList){
                    stringBuffer.append(stateDetail.getCHName()).append(":").append(stateDetail.getCHValue()).append("; ");
                }
                saveLogInfo(code, ModuleEnums.BASE, LogLevel.WARNING, LogType.WARNING_BASE, stringBuffer.toString());
            }
        }catch (Exception e){
            LOGGER.error("收集记录底盘日志,只记录触发状态. code="+code, e);
        }
    }

    /**
     * 收集记录导航日志
     */
    private void collectNavigationLog(String code) {
        List<StateDetail> stateDetails = stateCollectorService.getCurrentNavigationState(code);
        if (null != stateDetails && stateDetails.size() > 0) {
            StateDetail stateDetail = stateDetails.get(0);
            saveLogInfo(code, ModuleEnums.NAVIGATION, LogLevel.INFO, LogType.INFO_NAVIGATION, stateDetail.getCHValue());
        }
    }

    private String getChargeMessage(ChargeInfo chargeInfo) {
        StringBuffer stringBuffer = new StringBuffer();int powerPercent = chargeInfo.getPowerPercent();
        stringBuffer.append("当前电量:").append(powerPercent).append("%");

        int chargingStatus = chargeInfo.getChargingStatus();
        String chargingStatusStr = (chargingStatus == 0) ? "未充电" : "正在充电";
        stringBuffer.append(",充电状态：").append(chargingStatusStr).append("");

        int pluginStatus = chargeInfo.getPluginStatus();
        String pluginStatusStr = (pluginStatus == 0) ? "未插入充电桩" : "插入充电桩";
        stringBuffer.append(",充电桩状态：").append(pluginStatusStr);

        int autoCharging = chargeInfo.getAutoCharging();
        String autoChargingStr = (autoCharging == 0) ? "未插入充电桩" : "插入充电桩";
        stringBuffer.append(",自动回充状态：").append(autoChargingStr);
        return stringBuffer.toString();
    }

    private void collectTaskLog(String code){
        List<StateDetail> stateDetailList = stateCollectorService.collectTaskLog(code);

    }

    private void saveLogInfo(String code, ModuleEnums module, LogLevel logLevel, LogType logType, String message) {
        if(StringUtil.isNullOrEmpty(message)){
            return;
        }
        LogInfo logInfo = new LogInfo();
        logInfo.setCreateTime(new Date());
        logInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        logInfo.setDeviceId(code);
        logInfo.setLogLevel(logLevel.getName());
        logInfo.setModule(module.getModuleId());

        logInfo.setLogType(logType.getName());
        logInfo.setMessage(message);
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
        if (currentMap != null && !StringUtil.isNullOrEmpty(currentMap.getMessageText())) {
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            Integer currentMapCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
            if(currentMapCode != null && currentMapCode == 0 ){
                String mapData = object.getString(TopicConstants.DATA);
                JSONObject mapObject = JSON.parseObject(mapData);
                logInfo.setMapName(object.getString(TopicConstants.MAP_NAME));
                logInfo.setSceneName(object.getString(TopicConstants.SCENE_NAME));
            }
        }
        logInfoService.save(logInfo);
    }


}
