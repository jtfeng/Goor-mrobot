package cn.muye.base.model.config;


import java.io.Serializable;

/**
 * Created by ray.fu on 2017/5/9.
 */
public class RobotInfoConfig implements Serializable {

    private String robotSn;

    private String robotName;

    private Integer robotTypeId;

    private Integer lowBatteryThreshold; //机器人低电量阈值

    private Integer sufficientBatteryThreshold; //机器人足电量阈值

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

    public Long getRobotStoreId() {
        return robotStoreId;
    }

    public void setRobotStoreId(Long robotStoreId) {
        this.robotStoreId = robotStoreId;
    }
}
