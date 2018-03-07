package cn.mrobot.bean.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ray.fu on 2017/9/11.
 */
public enum AlertTypeEnum {

    ALERT_FREE_NAVIGATION_START_OVERTIME(10101, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHKSCS"),
    ALERT_FREE_NAVIGATION_CANCEL_OVERTIME(10102, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHQXCS"),
    ALERT_MP3_START_OVERTIME(10201, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_MP3KSCS"),
    ALERT_MP3_CANCEL_OVERTIME(10102, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_MP3QXCS"),
    ALERT_CHARGING_START_OVERTIME(10301, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_CDKSCS"),
    ALERT_CHARGING_CANCEL_OVERTIME(10302, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_CDQXCS"),
    ELEVATOR_START_OVERTIME(10401, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_DTKSCS"),
    ELEVATOR_CANCEL_OVERTIME(10402, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_DTQXCS"),
    ALERT_FIXED_PATH_NAVIGATION_START_OVERTIME(10501, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_GDLJDHKSCS"),
    ALERT_FIXED_PATH_NAVIGATION_CANCEL_OVERTIME(10102, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHQXCS"),
    ALERT_LOAD_START_OVERTIME(10601, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZHKSCS"),
    ALERT_LOAD_CANCEL_OVERTIME(10602, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHQXCS"),
    ALERT_FINAL_UNLOAD_START_OVERTIME(10701, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZDXHKSCS"),
    ALERT_FINAL_UNLOAD_CANCEL_OVERTIME(10702, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZDXHQXCS"),
    ALERT_ELEVATOR_LOCK_START_OVERTIME(10801, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_DTSKSCS"),
    ALERT_ELEVATOR_UNLOCK_START_OVERTIME(10901, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_DTJSKSCS"),
    ALERT_ROAD_LOCK_START_OVERTIME(11001, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_LJSKSCS"),
    ALERT_PATH_UNLOCK_START_OVERTIME(11101, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_LJJSKSCS"),
    ALERT_PATH_NAVIGATION_START_OVERTIME(11201, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_LJDHKSCS"),
    ALERT_PATH_NAVIGATION_CANCEL_OVERTIME(11201, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_LJDHQXCS"),
    ALERT_SEER_NAVIGATION_START_OVERTIME(11301, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_XZDHKSCS"),
    ALERT_PATH_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11401, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_GDLJGMKSKMCS"),
    ALERT_PATH_CROSS_DOOR_OVERTIME(11402, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_GDLJGMDHCS"),
    ALERT_PATH_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11403, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_GDLJGMGMCS"),

    ALERT_FREE_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11501, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHGMKSKMCS"),
    ALERT_FREE_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11502, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHGMDHCS"),
    ALERT_FREE_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11503, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_ZYDHGMGMCS"),

    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11601, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_YXDHGMKSKMCS"),
    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11602, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_YXDHGMDHCS"),
    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11603, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_YXDHGMGMCS"),

    ALERT_SEER_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11701, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_XZDHGMKSKMCS"),
    ALERT_SEER_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11702, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_XZDHGMDHCS"),
    ALERT_SEER_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11703, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_XZDHGMGMCS"),
    ALERT_GO_CHARGING_START_OVERTIME(11801, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_QCDKSCS"),
    ALERT_GO_CHARGING_CANCEL_OVERTIME(11802, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_QCDQXCS"),
    ALERT_LEAVE_CHARGER_START_OVERTIME(11901, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_LKCDZKSCS"),

    ALERT_ROBOT_OFFLINE_OVERTIME(20001, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_JQRYCLX"),
    ALERT_ROBOT_PATH_MOVE_OVERTIME(20002, "goor_domain_src_main_java_cn_mrobot_bean_alert_AlertTypeEnum_java_JQRZLSJCS");

    private int code;

    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getValue(String code) {
        String value = "";
        for (AlertTypeEnum noticeType : AlertTypeEnum.values()) {
            if (code.equals(String.valueOf(noticeType.getCode()))) {
                value = noticeType.getDescription();
            }
        }
        return value;
    }

    public static AlertTypeEnum getType(String description) {
        for (AlertTypeEnum c : AlertTypeEnum.values()) {
            if (c.getDescription() == description) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (AlertTypeEnum c : AlertTypeEnum.values()) {
            Map result = new HashMap<String, Object>();
            result.put("code", c.getCode());
            result.put("description", c.getDescription());
            resultList.add(result);
        }
        return resultList;
    }

    private AlertTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
