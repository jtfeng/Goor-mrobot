package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by abel on 17-7-8.
 */
@Table(name = "TASK_MISSION")
public class MissionTask extends BaseBean {
    private static final long serialVersionUID = 3913238395538242407L;

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
     * 预置任务编号
     */
    private String presetMissionCode;
    /**
     * 任务item列表，并行任务，不分顺序
     */

    @Transient
    private List<MissionItemTask> missionItemTasks;
    /**
     * 是否是order detail对应的任务，1:是; 0:不是
     */
    private String orderDetailMission;

    //下面的属性来自Mission
    private String name; //任务名
    private String description; //描述
    private Long intervalTime;

    public List<MissionItemTask> getMissionItemTasks() {
        return missionItemTasks;
    }

    public void setMissionItemTasks(List<MissionItemTask> missionItemTasks) {
        this.missionItemTasks = missionItemTasks;
    }

    public String getOrderDetailMission() {
        return orderDetailMission;
    }

    public void setOrderDetailMission(String orderDetailMission) {
        this.orderDetailMission = orderDetailMission;
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

    public String getPresetMissionCode() {
        return presetMissionCode;
    }

    public void setPresetMissionCode(String presetMissionCode) {
        this.presetMissionCode = presetMissionCode;
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

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }
}
