package cn.mrobot.bean.assets.robot;

import cn.mrobot.bean.robot.RobotPassword;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Table(name = "AS_ROBOT")
public class Robot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //机器人名称

    private String code; //机器人编号

    private Integer typeId; //机器人类型ID

    private String description;  //备注

    @Transient
    private Integer batteryThreshold; //机器人电量阈值

    private Boolean boxActivated; //启用和冻结（格子用的）

    @JSONField(format = "yyyy-MM-dd")
    private Date createTime; //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date updateTime; //修改时间

    @Transient
    private List<RobotPassword> passwords;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Integer getBatteryThreshold() {
        return batteryThreshold;
    }

    public void setBatteryThreshold(Integer batteryThreshold) {
        this.batteryThreshold = batteryThreshold;
    }
}
