package cn.muye.log.base;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.StateCollectorBaseDriver;
import cn.mrobot.bean.state.StateCollectorBaseMicroSwitchAndAntiDropping;
import cn.mrobot.bean.state.StateCollectorBaseSystem;
import cn.mrobot.bean.state.StateCollectorNavigation;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.bean.state.enums.NavigationType;
import cn.mrobot.bean.state.enums.StateFieldEnums;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.base.service.LogInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jelynn on 2017/7/24.
 */
@Service
public class LogCollectService {

    @Autowired
    private RobotService robotService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private LogInfoService logInfoService;

    public void startCollectLog() {
        List<Robot> robotList = robotService.listRobot(SearchConstants.FAKE_MERCHANT_STORE_ID);
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
     * 收集记录底盘日志
     */
    private void collectBaseLog(String code) {
        StringBuffer stringBuffer = new StringBuffer();
        try{
            //添加传感器状体日志
            StateCollectorBaseMicroSwitchAndAntiDropping baseMicroSwitchAndAntiDropping = CacheInfoManager.getBaseMicroSwitchAndAntiCache(code);
            Field[] fields = StateCollectorBaseMicroSwitchAndAntiDropping.class.getDeclaredFields();
            for(Field field : fields){
                int value = (int)field.get(baseMicroSwitchAndAntiDropping);
                if(value == 1){
                    stringBuffer.append(StateFieldEnums.getCHFieldName(field.getName())).append(": 触发");
                }
            }
            stringBuffer.append(System.getProperty("line.separator"));
            StateCollectorBaseSystem baseSystem = CacheInfoManager.getBaseSystemCache(code);
            Field[] baseSystemFields = StateCollectorBaseMicroSwitchAndAntiDropping.class.getDeclaredFields();
            for(Field field : baseSystemFields){
                int value = (int)field.get(baseSystem);
                if(value == 1 && (!field.getName().equals(StateFieldEnums.POWER_ON.getName()))){ //排除开机。开机为1
                    stringBuffer.append(StateFieldEnums.getCHFieldName(field.getName())).append(": 触发");
                }else if (value == 0 && field.getName().equals(StateFieldEnums.POWER_ON.getName())){
                    stringBuffer.append(StateFieldEnums.POWER_ON.getCHFieldName()).append(":开机");
                }
            }

            stringBuffer.append(System.getProperty("line.separator"));
            StateCollectorBaseDriver leftBaseDriver = CacheInfoManager.getLeftBaseDriverCache(code);
            Field[] leftBaseDriverFields = StateCollectorBaseMicroSwitchAndAntiDropping.class.getDeclaredFields();
            for(Field field : leftBaseDriverFields){
                int value = (int)field.get(leftBaseDriver);
                if(value == 1){
                    stringBuffer.append(StateFieldEnums.getCHFieldName(field.getName())).append(": 触发");
                }
            }

            stringBuffer.append(System.getProperty("line.separator"));
            StateCollectorBaseDriver rightBaseDriver =  CacheInfoManager.getRightBaseDriverCache(code);
            Field[] rightBaseDriverFields = StateCollectorBaseMicroSwitchAndAntiDropping.class.getDeclaredFields();
            for(Field field : rightBaseDriverFields){
                int value = (int)field.get(rightBaseDriver);
                if(value == 1){
                    stringBuffer.append(StateFieldEnums.getCHFieldName(field.getName())).append(": 触发");
                }
            }


            saveLogInfo(code, ModuleEnums.BASE, LogLevel.WARNING, LogType.WARNING_BASE, stringBuffer.toString());
        }catch (Exception e){

        }
    }

    /**
     * 收集记录导航日志
     */
    private void collectNavigationLog(String code) {
        StateCollectorNavigation navigation = CacheInfoManager.getNavigationCache(code);
        if(null == null){
            return;
        }
        int navigationTypeCode = navigation.getNavigationTypeCode();
        NavigationType type = NavigationType.getType(navigationTypeCode);
        String navigationTypeCodeStr = (type != null) ? type.getName() : "未定义状态";
        saveLogInfo(code, ModuleEnums.NAVIGATION, LogLevel.INFO, LogType.INFO_NAVIGATION, navigationTypeCodeStr);
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

    private void saveLogInfo(String code, ModuleEnums module, LogLevel logLevel, LogType logType, String message) {
        LogInfo logInfo = new LogInfo();
        logInfo.setCreateTime(new Date());
        logInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        logInfo.setDeviceId(code);
        logInfo.setLogLevel(logLevel.getName());
        logInfo.setModule(module.getModuleId());

        logInfo.setLogType(logType.getName());
        logInfo.setMessage(message);
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
        if(currentMap != null && !StringUtil.isNullOrEmpty(currentMap.getMessageText())){
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            String mapData = object.getString(TopicConstants.DATA);

            JSONObject mapDataObject = JSON.parseObject(mapData);
            logInfo.setMapName(mapDataObject.getString(TopicConstants.MAP_NAME));
            logInfo.setSceneName(mapDataObject.getString(TopicConstants.SCENE_NAME));
        }
        logInfoService.save(logInfo);
    }

    private void collectTaskLog(String code){

    }
}
