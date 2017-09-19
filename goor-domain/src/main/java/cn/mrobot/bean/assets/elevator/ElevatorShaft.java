package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "AS_ELEVATORSHAFT")
public class ElevatorShaft extends BaseBean {

    private String name;
    private String info;
    private String lockState = "0";// 0表示 未锁定、1表示 已锁定
    private String robotCode;
    @Transient
    private List<Elevator> elevators;

    @Override
    public String toString() {
        return "ElevatorShaft{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", elevators=" + elevators +
                ", lockState=" + lockState +
                ", robotCode=" + robotCode +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void setElevators(List<Elevator> elevators) {
        this.elevators = elevators;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    //电梯井动作
    public static enum ELEVATORSHAFT_ACTION {
        ELEVATORSHAFT_LOCK,
        ELEVATORSHAFT_UNLOCK
    }
}