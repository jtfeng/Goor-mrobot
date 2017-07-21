package cn.muye.base.model.config;


import java.io.Serializable;

/**
 * Created by enva on 2017/5/9.
 */
public class RobotInfoConfig implements Serializable {

    private String robotSn;

    private String robotName;

    private Integer robotTypeId;

    private Integer robotBatteryThreshold;

    private Long robotStoreId;

    public RobotInfoConfig(){

    }

    public String getRobotSn() {
        return robotSn;
    }

    public void setRobotSn(String robotSn) {
        this.robotSn = robotSn;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public Integer getRobotTypeId() {
        return robotTypeId;
    }

    public void setRobotTypeId(Integer robotTypeId) {
        this.robotTypeId = robotTypeId;
    }

    public Integer getRobotBatteryThreshold() {
        return robotBatteryThreshold;
    }

    public void setRobotBatteryThreshold(Integer robotBatteryThreshold) {
        this.robotBatteryThreshold = robotBatteryThreshold;
    }

    public Long getRobotStoreId() {
        return robotStoreId;
    }

    public void setRobotStoreId(Long robotStoreId) {
        this.robotStoreId = robotStoreId;
    }
}
