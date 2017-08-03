package cn.mrobot.bean.assets.robot;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Ray.Fu on 2017/6/19.
 */
@Table(name = "AS_ROBOT_CONFIG")
public class RobotConfig extends BaseBean {

    private Long robotId; //机器人ID

    private Integer lowBatteryThreshold; //机器人低电量阈值

    private Integer sufficientBatteryThreshold; //机器人足电量阈值

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
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
}
