package cn.muye.log.base.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.bean.StateDetail;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.log.base.service.LogCollectService;
import cn.muye.log.state.service.StateCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
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

        String code = robot.getCode();
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (null == robotConfig)
            return;

        ChargeInfo chargeInfo = CacheInfoManager.getRobotChargeInfoCache(code);
        if (chargeInfo == null) {
            return;
        }
        //保存电量信息
        LogInfoUtils.info(code, ModuleEnums.CHARGE, LogType.INFO_CHARGE, getChargeMessage(chargeInfo));

        //获取低电量阈值
        Integer lowBatteryThreshold = robotConfig.getLowBatteryThreshold();
        if (lowBatteryThreshold == null)
            return;
        //保存低电量警告
        int powerPercent = chargeInfo.getPowerPercent();
        if (powerPercent <= lowBatteryThreshold) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("电量阈值：").append(lowBatteryThreshold).append("%,当前电量：").append(powerPercent).append("%");
            LogInfoUtils.warn(code, ModuleEnums.CHARGE, LogType.WARNING_LOWER_POWER, stringBuffer.toString());
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
            LogInfoUtils.info(code, ModuleEnums.BASE, LogType.INFO_BASE, baseState);
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
        LogInfoUtils.info(code, ModuleEnums.NAVIGATION, LogType.INFO_NAVIGATION, navigationState);
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
        if (missionLog != null && missionLog.equals(missionState))
            return;

        if (StringUtil.isNullOrEmpty(missionLog))
            return;
        LogInfoUtils.info(code, ModuleEnums.MISSION, LogType.INFO_SCHEDULE_TASK, missionLog);
        //将已存库的任务状态放入缓存中，以便下一次比对
        CacheInfoManager.setPersistMissionState(code, missionLog);
    }
}
