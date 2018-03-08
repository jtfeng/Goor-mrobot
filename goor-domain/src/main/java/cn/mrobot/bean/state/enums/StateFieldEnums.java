package cn.mrobot.bean.state.enums;

import cn.mrobot.bean.assets.rfidbracelet.RfidBraceletTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 状态收集属性的中英文名称对照表
 * Created by Jelynn on 2017/7/24.
 */
public enum StateFieldEnums {

    //底盘系统
    RES("res","RES"),
    POWER_ON("powerOn","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_DPKGJ"),
    NORMAL("normal","normal"),
    IO_EMERGENCY_STOP("IOEmergencyStop","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_IOJT"),
    SWITCH_EMERGENCY_STOP("switchEmergencyStop","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_KGJT"),
    UNDER_VOLTAGE_EMERGENCY_STOP("underVoltageEmergencyStop","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_QYTJ"),
    OVER_SPEED_EMERGENCY_STOP("overSpeedEmergencyStop","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_GSTJ"),

    //驱动器状态
    LEFT_DRIVER_FLOW("driverFlow","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_QDQGL"),
    LEFT_DRIVER_ERROR("driverError","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_BMQCW"),
    LEFT_POOR_POSITION("poorPosition","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_WZCC"),
    LEFT_DRIVER_OVERLOAD("driverOverload","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_QDQGZ"),
    LEFT_MOTOR_HIGH_TEMPERATURE("motorHighTemperature","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_DJGW"),
    LEFT_MOTOR_COMMUNICATION_BREAK("motorCommunicationBreak","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_DJTXDX"),
    LEFT_PWM_CONTROLL_BREAK("PWMControllBreak","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_PWMKZDX"),

//    //左驱动器状态
//    LEFT_DRIVER_FLOW("leftDriverFlow","左驱动器过流"),
//    LEFT_DRIVER_ERROR("leftDriverError","左编码器错误"),
//    LEFT_POOR_POSITION("leftPoorPosition","左驱动器位置超差"),
//    LEFT_DRIVER_OVERLOAD("leftDriverOverload","左驱动器过载"),
//    LEFT_MOTOR_HIGH_TEMPERATURE("leftMotorHighTemperature","左驱动器电机过温"),
//    LEFT_MOTOR_COMMUNICATION_BREAK("leftMotorCommunicationBreak","左驱动器电机通信断线"),
//    LEFT_PWM_CONTROLL_BREAK("leftPWMControllBreak","左驱动器PWM控制断线"),

//    //右驱动器状态
//    RIGHT_DRIVER_FLOW("rightDriverFlow","右驱动器过流"),
//    RIGHT_DRIVER_ERROR("rightDriverError","右驱动编码器错误"),
//    RIGHT_POOR_POSITION("rightPoorPosition","右驱动位置超差"),
//    RIGHT_DRIVER_OVERLOAD("rightDriverOverload","右驱动驱动器过载"),
//    RIGHT_MOTOR_HIGH_TEMPERATURE("rightMotorHighTemperature","右驱动电机过温"),
//    RIGHT_MOTOR_COMMUNICATION_BREAK("rightMotorCommunicationBreak","右驱动电机通信断线"),
//    RIGHT_PWM_CONTROLL_BREAK("rightPWMControllBreak","右驱动PWM控制断线"),

    //防跌落
    LEFT_ANTI_DROPPING("leftAntiDropping","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FDLZCGQ"),
    MIDDLE_ANTI_DROPPING("middleAntiDropping","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FDLZCGQ1519972766322"),
    RIGHT_ANTI_DROPPING("rightAntiDropping","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FDLYCGQ"),

    //防碰撞
    LEFT_BASE_MICRO_SWITCH("leftBaseMicroSwitch","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FPZZKG"),
    MIDDLE_BASE_MICRO_SWITCH("middleBaseMicroSwitch","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FPZZKG1519972766323"),
    RIGHT_BASE_MICRO_SWITCH("rightBaseMicroSwitch","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_FPZYKG"),

    //自动导航
    NAVIGATION_TYPE_CODE("navigationTypeCode","goor_domain_src_main_java_cn_mrobot_bean_state_enums_StateFieldEnums_java_ZDDH")
    ;


    private String name;
    private String CHFieldName;

    StateFieldEnums(String name, String CHFieldName) {
        this.name = name;
        this.CHFieldName = CHFieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCHFieldName() {
        return CHFieldName;
    }

    public void setCHFieldName(String CHFieldName) {
        this.CHFieldName = CHFieldName;
    }

    public static StateFieldEnums getType(String name) {
        for (StateFieldEnums stateFieldEnums : StateFieldEnums.values()) {
            if (stateFieldEnums.getName().equals(name)) {
                return stateFieldEnums;
            }
        }
        return null;
    }

    public static String getCHFieldName(String name) {
        for (StateFieldEnums stateFieldEnums : StateFieldEnums.values()) {
            if (stateFieldEnums.getName().equals(name)) {
                return stateFieldEnums.getCHFieldName();
            }
        }
        return "";
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (StateFieldEnums c : StateFieldEnums.values()) {
            resultList.add(toDTO(c)) ;
        }
        return resultList;
    }

    private static Map toDTO(StateFieldEnums c) {
        Map result = new HashMap<String,Object>();
        result.put("name",c.getName());
        result.put("cHFieldName",c.getCHFieldName());
        return result;
    }
}
