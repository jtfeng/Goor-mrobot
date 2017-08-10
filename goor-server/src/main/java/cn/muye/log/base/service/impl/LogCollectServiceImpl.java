package cn.muye.log.base.service.impl;

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
import cn.muye.log.base.service.LogCollectService;
import cn.muye.log.base.service.LogInfoService;
import cn.muye.log.state.service.StateCollectorService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jelynn on 2017/7/24.
 */
@Service
public class LogCollectServiceImpl implements LogCollectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCollectService.class);
    @Autowired
    private RobotService robotService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private LogInfoService logInfoService;

    @Autowired
    private StateCollectorService stateCollectorService;

    @Override
    public void startCollectLog() {
        List<Robot> robotList = robotService.listRobot(SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (robotList == null || robotList.size() <= 0) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(robotList.size());
        for (int i = 0; i < robotList.size(); i++) {
            Robot robot = robotList.get(i);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    collectRobotCurrentState(robot);
                }
            });
            executorService.execute(thread);
        }
        executorService.shutdown();
    }


    private void collectRobotCurrentState(Robot robot) {
        if (null == robot) {
            return;
        }
        String code = robot.getCode();
        collectChargeLog(robot);
        collectBaseLog(code);
        collectNavigationLog(code);
        collectMissionLog(code);
    }

    /**
     * 收集记录电量日志
     */
    private void collectChargeLog(Robot robot) {
        String chargeState = "";
        String code = robot.getCode();
        ChargeInfo chargeInfo = CacheInfoManager.getRobotChargeInfoCache(code);
        if (chargeInfo == null) {
            return;
        }
        //获取电量状态
        chargeState = getChargeMessage(chargeInfo);
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (robotConfig == null) {
            return;
        }
        Integer batteryThreshold = robotConfig.getLowBatteryThreshold();
        if (batteryThreshold == null) {
            return;
        }
        LogInfo logInfo = new LogInfo();
        logInfo.setLogLevel(LogLevel.INFO.getName());
        logInfo.setModule(ModuleEnums.CHARGE.getModuleId());
        logInfo.setLogType(LogType.INFO_CHARGE.getName());
        logInfo.setMessage(chargeState);
        saveLogInfo(code, logInfo);
        //保存低电量警告
        int powerPercent = chargeInfo.getPowerPercent();
        if (powerPercent <= batteryThreshold) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("电量阈值：").append(batteryThreshold).append("%,当前电量：").append(powerPercent).append("%");
            LogInfo warningLogInfo = new LogInfo();
            warningLogInfo.setLogLevel(LogLevel.WARNING.getName());
            warningLogInfo.setModule(ModuleEnums.CHARGE.getModuleId());
            warningLogInfo.setLogType(LogType.WARNING_LOWER_POWER.getName());
            warningLogInfo.setMessage(stringBuffer.toString());
            saveLogInfo(code, warningLogInfo);
        }
    }

    /**
     * 收集记录底盘日志,只记录触发状态
     */
    private void collectBaseLog(String code) {
        try {
            List<StateDetail> stateDetailList = stateCollectorService.getCurrentBaseState(true, code);
            StringBuffer stringBuffer = new StringBuffer();
            if (stateDetailList != null && stateDetailList.size() > 0) {
                for (StateDetail stateDetail : stateDetailList) {
                    stringBuffer.append(stateDetail.getCHName()).append(":").append(stateDetail.getCHValue()).append(", ");
                }
            }
            String baseState = stringBuffer.toString();
            if (StringUtil.isNullOrEmpty(baseState))
                return;
            LogInfo logInfo = new LogInfo();
            logInfo.setLogLevel(LogLevel.INFO.getName());
            logInfo.setModule(ModuleEnums.BASE.getModuleId());
            logInfo.setLogType(LogType.INFO_BASE.getName());
            logInfo.setMessage(baseState);
            saveLogInfo(code, logInfo);
        } catch (Exception e) {
            LOGGER.error("收集底盘出错. code=" + code, e);
        }
    }

    /**
     * 收集记录导航日志
     */
    private void collectNavigationLog(String code) {
        List<StateDetail> stateDetails = stateCollectorService.getCurrentNavigationState(code);
        StringBuffer stringBuffer = new StringBuffer();
        if (null != stateDetails && stateDetails.size() > 0) {
            StateDetail stateDetail = stateDetails.get(0);
            stringBuffer.append(ModuleEnums.NAVIGATION.getModuleName()).append(":").append(stateDetail.getCHValue()).append(",");
        }
        String navigationState = stringBuffer.toString();
        if (StringUtil.isNullOrEmpty(navigationState))
            return;
        LogInfo logInfo = new LogInfo();
        logInfo.setLogLevel(LogLevel.INFO.getName());
        logInfo.setModule(ModuleEnums.NAVIGATION.getModuleId());
        logInfo.setLogType(LogType.INFO_NAVIGATION.getName());
        logInfo.setMessage(navigationState);
        saveLogInfo(code, logInfo);
    }

    private String getChargeMessage(ChargeInfo chargeInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        int powerPercent = chargeInfo.getPowerPercent();
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

    //TODO
    private void collectMissionLog(String code) {
        List<StateDetail> stateDetailList = stateCollectorService.collectTaskLog(code);
        if (stateDetailList == null || stateDetailList.size() <= 0)
            return;
        //按创建时间排序
        Collections.sort(stateDetailList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                StateDetail stateDetail1 = (StateDetail) o1;
                StateDetail stateDetail2 = (StateDetail) o2;
                return stateDetail1.getCreateTime().compareTo(stateDetail2.getCreateTime());
            }
        });
        //按照时间顺序添加日志
        StringBuffer stringBuffer = new StringBuffer();
        for (StateDetail stateDetail : stateDetailList) {
            stringBuffer.append(stateDetail.getCHName()).append(" ").append(stateDetail.getCHValue()).append(",");
        }
        String missionLog = stringBuffer.toString();

        //校验是否已经存库
        String missionState = CacheInfoManager.getPersistMissionState(code);
        if(missionLog != null && missionLog.equals(missionState))
            return;

        if(StringUtil.isNullOrEmpty(missionLog))
            return;
        LogInfo logInfo = new LogInfo();
        logInfo.setLogLevel(LogLevel.INFO.getName());
        logInfo.setModule(ModuleEnums.MISSION.getModuleId());
        logInfo.setLogType(LogType.INFO_SCHEDULE_TASK.getName());
        logInfo.setMessage(missionLog);
        saveLogInfo(code, logInfo);

        //将已存库的任务状态放入缓存中，以便下一次比对
        CacheInfoManager.setPersistMissionState(code, missionLog);
    }

    private void saveLogInfo(String code, LogInfo logInfo) {
        logInfo.setDeviceId(code);
        logInfo.setCreateTime(new Date());
        logInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
        if (currentMap != null && !StringUtil.isNullOrEmpty(currentMap.getMessageText())) {
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            Integer currentMapCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
            if (currentMapCode != null && currentMapCode == 0) {
                String mapData = object.getString(TopicConstants.DATA);
                JSONObject mapObject = JSON.parseObject(mapData);
                logInfo.setMapName(mapObject.getString(TopicConstants.MAP_NAME));
                logInfo.setSceneName(mapObject.getString(TopicConstants.SCENE_NAME));
            }
        }
        logInfoService.save(logInfo);
    }
}
