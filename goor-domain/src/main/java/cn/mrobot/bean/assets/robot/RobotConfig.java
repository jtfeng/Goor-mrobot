package cn.mrobot.bean.assets.robot;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Ray.Fu on 2017/6/19.
 */
@Table(name = "AS_ROBOT_CONFIG")
public class RobotConfig extends BaseBean {

    private Long robotId; //机器人ID

    private Integer batteryThreshold; //机器人电量阈值

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public Integer getBatteryThreshold() {
        return batteryThreshold;
    }

    public void setBatteryThreshold(Integer batteryThreshold) {
        this.batteryThreshold = batteryThreshold;
    }
}
