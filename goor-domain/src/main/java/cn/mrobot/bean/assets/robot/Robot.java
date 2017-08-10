package cn.mrobot.bean.assets.robot;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.mission.task.JsonMissionItemDataLaserNavigation;
import com.alibaba.fastjson.annotation.JSONField;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Table(name = "AS_ROBOT")
public class Robot extends BaseBean {

    @Transient
    private String uuid;//uuid消息唯一标识符

    private String name; //机器人名称

    private String code; //机器人编号

    private Integer typeId; //机器人类型ID

    private String description;  //备注

    @Transient
    private Integer lowBatteryThreshold; //机器人低电量阈值

    @Transient
    private Integer sufficientBatteryThreshold; //机器人足电量阈值

    private Boolean boxActivated; //启用和冻结（格子用的）

    @JSONField(format = "yyyy-MM-dd")
    private Date updateTime; //修改时间

    private Boolean isBusy = Boolean.FALSE; ; //状态(0-空闲， 1-占用)

    private Boolean isOnline = Boolean.TRUE; //在线状态

    @Transient
    private String sceneName; //场景名

    @Transient
    private Long sceneId; //场景ID

    @Transient
    private List<RobotPassword> passwords; //机器人抽屉密码

    @Transient
    private List<MapPoint> originChargerMapPointList; //充电桩点LIST (数据库里查出来的充电桩点)

    @Transient
    private List<JsonMissionItemDataLaserNavigation> chargerMapPointList; //充电桩点LIST(转换成任务管理需要的数据格式)

    private Integer robotIdForElevator; //机器人电梯编号（针对电梯使用）

    public List<RobotPassword> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<RobotPassword> passwords) {
        this.passwords = passwords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getBoxActivated() {
        return boxActivated;
    }

    public void setBoxActivated(Boolean boxActivated) {
        this.boxActivated = boxActivated;
    }

    public Integer getLowBatteryThreshold() {
        return lowBatteryThreshold;
    }

    public void setLowBatteryThreshold(Integer lowBatteryThreshold) {
        this.lowBatteryThreshold = lowBatteryThreshold;
    }

    public Integer getSufficientBatteryThreshold() {
        return sufficientBatteryThreshold;
    }

    public void setSufficientBatteryThreshold(Integer sufficientBatteryThreshold) {
        this.sufficientBatteryThreshold = sufficientBatteryThreshold;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }

    public List<MapPoint> getOriginChargerMapPointList() {
        return originChargerMapPointList;
    }

    public void setOriginChargerMapPointList(List<MapPoint> originChargerMapPointList) {
        this.originChargerMapPointList = originChargerMapPointList;
    }

    public List<JsonMissionItemDataLaserNavigation> getChargerMapPointList() {
        return chargerMapPointList;
    }

    public void setChargerMapPointList(List<JsonMissionItemDataLaserNavigation> chargerMapPointList) {
        this.chargerMapPointList = chargerMapPointList;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getRobotIdForElevator() {
        return robotIdForElevator;
    }

    public void setRobotIdForElevator(Integer robotIdForElevator) {
        this.robotIdForElevator = robotIdForElevator;
    }
}
