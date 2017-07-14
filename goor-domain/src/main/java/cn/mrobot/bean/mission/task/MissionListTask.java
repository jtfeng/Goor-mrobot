package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.base.BaseBean;

import java.beans.Transient;
import java.util.List;

/**
 * Created by abel on 17-7-8.
 */
public class MissionListTask extends BaseBean {

    private static final long serialVersionUID = -2085693663353173366L;
    /**
     *任务列表ID
     */
    private Long missionListId;
    /**
     * 任务执行状态
     */
    private String state;
    /**
     * 重复执行次数
     */
    private Integer repeatTimes;
    /**
     * 重复执行次数实时状态，查询状态时候的repeat_times值放到该字段
     */
    private Integer repeatTimesReal;
    /**
     * 机器人编号
     */
    private String robotCode;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 任务列表
     */
    private List<MissionTask> missionTasks;

    //下面属性来自MissionList
    private String name;  //总任务名称
    private String description;
    private String missionListType;
    /**
     * //间隔时间
     */
    private Long intervalTime;
    private Long startTime;
    private Long stopTime;
    private Integer priority;//优先级

    @Transient
    public List<MissionTask> getMissionTasks() {
        return missionTasks;
    }

    public void setMissionTasks(List<MissionTask> missionTasks) {
        this.missionTasks = missionTasks;
    }

    public Long getMissionListId() {
        return missionListId;
    }

    public void setMissionListId(Long missionListId) {
        this.missionListId = missionListId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public Integer getRepeatTimesReal() {
        return repeatTimesReal;
    }

    public void setRepeatTimesReal(Integer repeatTimesReal) {
        this.repeatTimesReal = repeatTimesReal;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMissionListType() {
        return missionListType;
    }

    public void setMissionListType(String missionListType) {
        this.missionListType = missionListType;
    }

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
