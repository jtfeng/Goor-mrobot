package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.base.BaseBean;

/**
 * Created by abel on 17-7-8.
 */
public class MissionItemTask extends BaseBean {

    private static final long serialVersionUID = 8828205395122968816L;
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
     * 任务执行状态
     */
    private String state;

    //下面属性来自MissionItem
    private String name;
    private String description; //描述
    private String data;//任务详细/功能数据
    private String featureValue;//data对应子功能的唯一命令字串

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }
}
