package cn.muye.log.state;

import cn.mrobot.bean.state.*;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import org.springframework.stereotype.Service;

/**
 * 解析状态机数据类
 * Created by Jelynn on 2017/7/18.
 */
@Service
public class StateCollectorService {

    private static final int MODULE_SLAM_NAVI = 100; //导航
    private static final int MODULE_AUTO_CHARGING = 200; //自动回充
    private static final int MODULE_BASE_LEFT = 301; //左驱动器状态
    private static final int MODULE_BASE_RIGHT = 302;//右驱动器状态
    private static final int MODULE_BASE_SYSTEM = 303;//底盘系统状态
    private static final int MODULE_BASE_MICROSWITCH_AND_ANTI_DROPPING = 304;//微动开关与防跌落状态

    public void handleStateCollector(StateCollectorResponse stateCollectorResponse) {
        String module = stateCollectorResponse.getModule();
        int moduleId = Integer.parseInt(module);
        switch (moduleId) {
            case MODULE_SLAM_NAVI:
                analysisNavigationState(stateCollectorResponse);
                break;
            case MODULE_AUTO_CHARGING:
                analysisAutoChargingState(stateCollectorResponse);
                break;
            case MODULE_BASE_LEFT:
            case MODULE_BASE_RIGHT:
                analysisBasDriverState(stateCollectorResponse);
                break;
            case MODULE_BASE_SYSTEM:
                analysisBaseSystemState(stateCollectorResponse);
                break;
            case MODULE_BASE_MICROSWITCH_AND_ANTI_DROPPING:
                analysisBaseMicroswitchAndAntiDroppingstate(stateCollectorResponse);
                break;
            default:
                break;
        }
    }

    /**
     * 解析导航状态数据
     */
    private void analysisNavigationState(StateCollectorResponse stateCollectorResponse) {
        StateCollectorNavigation stateCollectorNavigation = new StateCollectorNavigation();
        stateCollectorNavigation.setCreateTime(stateCollectorResponse.getTime());
        stateCollectorNavigation.setNavigationTypeCode(Integer.parseInt(stateCollectorResponse.getState()));
        CacheInfoManager.setNavigationCache(stateCollectorResponse.getSenderId(), stateCollectorNavigation);
    }

    /**
     * 解析自动回充状态数据
     */
    private void analysisAutoChargingState(StateCollectorResponse stateCollectorResponse) {
        StateCollectorAutoCharge stateCollectorAutoCharge = new StateCollectorAutoCharge();
        stateCollectorAutoCharge.setCreateTime(stateCollectorResponse.getTime());
        stateCollectorAutoCharge.setPluginStatus(Integer.parseInt(stateCollectorResponse.getState()));
        CacheInfoManager.setAutoChargeCache(stateCollectorResponse.getSenderId(), stateCollectorAutoCharge);
    }

    /**
     * 解析驱动器状态数据
     */
    private void analysisBasDriverState(StateCollectorResponse stateCollectorResponse) {
        int moduleId = Integer.parseInt(stateCollectorResponse.getModule());
        StateCollectorBaseDriver stateCollectorBaseDriver = new StateCollectorBaseDriver();
        String stateStr = StringUtil.parseToBit(stateCollectorResponse.getState()); //00011011
        char[] chars = stateStr.toCharArray();
        int length = chars.length;
        stateCollectorBaseDriver.setDriverFlow(parseInt(chars[length - 1]));//驱动器过流  0：未触发  1：触发
        stateCollectorBaseDriver.setDriverError(parseInt(chars[length - 2]));//编码器错误
        stateCollectorBaseDriver.setPoorPosition(parseInt(chars[length - 3]));//位置超差
        stateCollectorBaseDriver.setDriverOverload(parseInt(chars[length - 4]));//驱动器过载
        stateCollectorBaseDriver.setMotorHighTemperature(parseInt(chars[length - 5]));//电机过温
        stateCollectorBaseDriver.setMotorCommunicationBreak(parseInt(chars[length - 6]));//电机通信断线
        stateCollectorBaseDriver.setPWMControllBreak(parseInt(chars[length - 7]));//PWM控制断线
        String deviceId = stateCollectorResponse.getSenderId();
        if (moduleId == MODULE_BASE_LEFT) {
            CacheInfoManager.setLeftBaseDriverCache(deviceId, stateCollectorBaseDriver);
        } else if (moduleId == MODULE_BASE_RIGHT) {
            CacheInfoManager.setRightBaseDriverCache(deviceId, stateCollectorBaseDriver);
        }
    }

    /**
     * 解析底盘系统状态数据
     */
    private void analysisBaseSystemState(StateCollectorResponse stateCollectorResponse) {
        StateCollectorBaseSystem stateCollectorBaseSystem = new StateCollectorBaseSystem();
        String stateStr = StringUtil.parseToBit(stateCollectorResponse.getState()); //00011011
        char[] chars = stateStr.toCharArray();
        int length = chars.length;
        stateCollectorBaseSystem.setRes(parseInt(chars[length - 1])); //res 0：未触发  1：触发
        stateCollectorBaseSystem.setPowerOn(parseInt(chars[length - 2]));//开机
        stateCollectorBaseSystem.setNormal(parseInt(chars[length - 3]));//正常
        stateCollectorBaseSystem.setIOEmergencyStop(parseInt(chars[length - 4]));//IO急停
        stateCollectorBaseSystem.setSwitchEmergencyStop(parseInt(chars[length - 5]));//开关急停
        stateCollectorBaseSystem.setUnderVoltageEmergencyStop(parseInt(chars[length - 6]));//欠压停机
        stateCollectorBaseSystem.setOverSpeedEmergencyStop(parseInt(chars[length - 7]));//过速停机
        CacheInfoManager.setBaseSystemCache(stateCollectorResponse.getSenderId(), stateCollectorBaseSystem);
    }

    /**
     * 解析防碰撞传感器,与防跌落传感器状态数据 x000x000  (x为占位符吗，始终为0，从左到右依次是左、中、右传感器)
     */
    private void analysisBaseMicroswitchAndAntiDroppingstate(StateCollectorResponse stateCollectorResponse) {
        StateCollectorBaseMicroSwitchAndAntiDropping baseMicroSwitchAndAntiDropping = new StateCollectorBaseMicroSwitchAndAntiDropping();
        String stateStr = StringUtil.parseToBit(stateCollectorResponse.getState());
        char[] chars = stateStr.toCharArray();
        int length = chars.length;
        baseMicroSwitchAndAntiDropping.setLeftAntiDropping(parseInt(chars[length -1]));//防跌落左传感器
        baseMicroSwitchAndAntiDropping.setMiddleAntiDropping(parseInt(chars[length -2]));//防跌落中传感器
        baseMicroSwitchAndAntiDropping.setRightAntiDropping(parseInt(chars[length -3]));//防跌落右传感器
        baseMicroSwitchAndAntiDropping.setLeftBaseMicroSwitch(parseInt(chars[length -5])); //防碰撞左开关
        baseMicroSwitchAndAntiDropping.setMiddleBaseMicroSwitch(parseInt(chars[length -6]));//防碰撞中开关
        baseMicroSwitchAndAntiDropping.setRightBaseMicroSwitch(parseInt(chars[length -7]));//防碰撞右开关
        CacheInfoManager.setBaseMicroSwitchAndAntiCache(stateCollectorResponse.getSenderId(), baseMicroSwitchAndAntiDropping);
    }

    private int parseInt(char c){
        return Integer.parseInt(String.valueOf(c));
    }
}
