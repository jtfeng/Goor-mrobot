package cn.mrobot.bean.log.mission;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;

/**
 * Created by abel on 17-7-7.
 */
@Table(name = "LOG_MISSION")
public class LogMission extends BaseBean {
    private static final long serialVersionUID = -5855449136015060858L;

    public enum MissionLogType{
        MISSION_LIST,
        MISSION,
        MISSION_ITEM,
        NOT_USE;
        public static MissionLogType valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                return NOT_USE;
            }
            return values()[ordinal];
        }
    }

    public static final String event_start_success = "start_success";
    public static final String event_start_fail = "start_fail";
    public static final String event_pause_success = "pause_success";
    public static final String event_pause_fail = "pause_fail";
    public static final String event_resume_success = "resume_success";
    public static final String event_resume_fail = "resume_fail";
    public static final String event_cancel_success = "cancel_success";
    public static final String event_cancel_fail = "cancel_fail";
    public static final String event_finish = "finish";
    public static final String event_fail = "fail";


    public static final String object_mission_list = "mission_list";
    public static final String object_mission = "mission";
    public static final String object_mission_item = "mission_item";

    /**
     * 机器人编号
     */
    private String robotCode;

    /**
     *任务日志类型：0-任务列表日志，1-任务日志，2-任务节点日志
     */
    private Integer missionType;
    /**
     *任务列表ID
     */
    private Long missionListId;
    /**
     *任务ID
     */
    private Long missionId;
    /**
     *任务节点ID
     */
    private Long missionItemId;
    /**
     * 任务item的name
     */
    private String missionItemName;
    /**
     *任务列表重复
     */
    private Integer missionListRepeatTimes;
    /**
     * 任务重复
     */
    private Integer missionRepeatTimes;
    /**
     * event 目前包括（后续可能增加）：
     start_success：开始成功
     start_fail：开始失败
     pause_success：暂停成功
     pause_fail：暂停失败
     resume_success：恢复成功
     resume_fail：恢复失败
     cancel_success：取消成功
     cancel_fail：取消失败
     finish：完成
     */
    private String missionEvent;
    /**
     * 事件描述，对于特殊的事件加以说明，若无说明则为空字符串
     */
    private String missionDescription;

    private int chargingStatus; //充电状态  1：正在充电  0：未充电

    private int pluginStatus; // 1：插入充电桩   0：未插入充电桩

    private int powerPercent;  //电量  范围  0-100

    private String ros; //ros当前位置信息

    public String getMissionItemName() {
        return missionItemName;
    }

    public void setMissionItemName(String missionItemName) {
        this.missionItemName = missionItemName;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Integer getMissionType() {
        return missionType;
    }

    public void setMissionType(Integer missionType) {
        this.missionType = missionType;
    }

    public Long getMissionListId() {
        return missionListId;
    }

    public void setMissionListId(Long missionListId) {
        this.missionListId = missionListId;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Long getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Long missionItemId) {
        this.missionItemId = missionItemId;
    }

    public Integer getMissionListRepeatTimes() {
        return missionListRepeatTimes;
    }

    public void setMissionListRepeatTimes(Integer missionListRepeatTimes) {
        this.missionListRepeatTimes = missionListRepeatTimes;
    }

    public Integer getMissionRepeatTimes() {
        return missionRepeatTimes;
    }

    public void setMissionRepeatTimes(Integer missionRepeatTimes) {
        this.missionRepeatTimes = missionRepeatTimes;
    }

    public String getMissionEvent() {
        return missionEvent;
    }

    public void setMissionEvent(String missionEvent) {
        this.missionEvent = missionEvent;
    }

    public String getMissionDescription() {
        return missionDescription;
    }

    public void setMissionDescription(String missionDescription) {
        this.missionDescription = missionDescription;
    }

    public int getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(int chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public int getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(int pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

    public int getPowerPercent() {
        return powerPercent;
    }

    public void setPowerPercent(int powerPercent) {
        this.powerPercent = powerPercent;
    }

    public String getRos() {
        return ros;
    }

    public void setRos(String ros) {
        this.ros = ros;
    }
}
