package cn.mrobot.bean.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ray.fu on 2017/9/11.
 */
public enum AlertTypeEnum {

    ALERT_FREE_NAVIGATION_START_OVERTIME(10101, "自由导航开始超时"),
    ALERT_FREE_NAVIGATION_CANCEL_OVERTIME(10102, "自由导航取消超时"),
    ALERT_MP3_START_OVERTIME(10201, "mp3开始超时"),
    ALERT_MP3_CANCEL_OVERTIME(10102, "mp3取消超时"),
    ALERT_CHARGING_START_OVERTIME(10301, "充电开始超时"),
    ALERT_CHARGING_CANCEL_OVERTIME(10302, "充电取消超时"),
    ELEVATOR_START_OVERTIME(10401, "电梯开始超时"),
    ELEVATOR_CANCEL_OVERTIME(10402, "电梯取消超时"),
    ALERT_FIXED_PATH_NAVIGATION_START_OVERTIME(10501, "固定路径导航开始超时"),
    ALERT_FIXED_PATH_NAVIGATION_CANCEL_OVERTIME(10102, "自由导航取消超时"),
    ALERT_LOAD_START_OVERTIME(10601, "装货开始超时"),
    ALERT_LOAD_CANCEL_OVERTIME(10602, "自由导航取消超时"),
    ALERT_FINAL_UNLOAD_START_OVERTIME(10701, "终点卸货开始超时"),
    ALERT_FINAL_UNLOAD_CANCEL_OVERTIME(10702, "终点卸货取消超时"),
    ALERT_ELEVATOR_LOCK_START_OVERTIME(10801, "电梯锁开始超时"),
    ALERT_ELEVATOR_UNLOCK_START_OVERTIME(10901, "电梯解锁开始超时"),
    ALERT_ROAD_LOCK_START_OVERTIME(11001, "路径锁开始超时"),
    ALERT_PATH_UNLOCK_START_OVERTIME(11101, "路径解锁开始超时"),
    ALERT_PATH_NAVIGATION_START_OVERTIME(11201, "路径导航开始超时"),
    ALERT_PATH_NAVIGATION_CANCEL_OVERTIME(11201, "路径导航取消超时"),
    ALERT_SEER_NAVIGATION_START_OVERTIME(11301, "仙知导航开始超时"),
    ALERT_PATH_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11401, "固定路径过门开始开门超时"),
    ALERT_PATH_CROSS_DOOR_OVERTIME(11402, "固定路径过门导航超时"),
    ALERT_PATH_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11403, "固定路径过门关门超时"),

    ALERT_FREE_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11501, "自由导航过门开始开门超时"),
    ALERT_FREE_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11502, "自由导航过门导航超时"),
    ALERT_FREE_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11503, "自由导航过门关门超时"),

    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11601, "沿线导航过门开始开门超时"),
    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11602, "沿线导航过门导航超时"),
    ALERT_ALONG_LANE_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11603, "沿线导航过门关门超时"),

    ALERT_SEER_NAVIGATION_CROSS_DOOR_START_OPEN_DOOR_OVERTIME(11701, "仙知导航过门开始开门超时"),
    ALERT_SEER_NAVIGATION_CROSS_DOOR_NAVIGATION_OVERTIME(11702, "仙知导航过门导航超时"),
    ALERT_SEER_NAVIGATION_CROSS_DOOR_CLOSE_DOOR_OVERTIME(11703, "仙知导航过门关门超时"),
    ALERT_GO_CHARGING_START_OVERTIME(11801, "去充电开始超时"),
    ALERT_GO_CHARGING_CANCEL_OVERTIME(11802, "去充电取消超时"),
    ALERT_LEAVE_CHARGER_START_OVERTIME(11901, "离开充电桩开始超时"),

    ALERT_ROBOT_OFFLINE_OVERTIME(20001, "机器人异常离线"),
    ALERT_ROBOT_PATH_MOVE_OVERTIME(20002, "机器人滞留时间超时");

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
