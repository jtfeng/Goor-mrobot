package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Table(name = "AS_ELEVATOR")
public class Elevator extends BaseBean {
    private String name;
    private String ip;
    private String lockState = "0";// 0表示 未锁定、1表示 已锁定
    private String info;
    private Long elevatorshaftId;
    private String robotCode;
    @Transient
    private ElevatorShaft elevatorShaft;

    private String ipElevatorId; //工控电梯ID （八位二进制）

    private Boolean defaultElevator = Boolean.FALSE; //是否默认

    @Transient
    private List<ElevatorPointCombination> elevatorPointCombinations = new ArrayList<>();

    @Override
    public String toString() {
        return "Elevator{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", lockState='" + lockState + '\'' +
                ", info='" + info + '\'' +
                ", robotCode='" + robotCode + '\'' +
                ", elevatorshaftId=" + elevatorshaftId +
                ", robotCode='" + robotCode + '\'' +
                ", elevatorShaft=" + elevatorShaft +
                ", ipElevatorId=" + ipElevatorId +
                ", defaultElevator=" + defaultElevator +
                ", elevatorPointCombinations=" + elevatorPointCombinations +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ElevatorShaft getElevatorShaft() {
        return elevatorShaft;
    }

    public void setElevatorShaft(ElevatorShaft elevatorShaft) {
        this.elevatorShaft = elevatorShaft;
    }

    public List<ElevatorPointCombination> getElevatorPointCombinations() {
        return elevatorPointCombinations;
    }

    public void setElevatorPointCombinations(List<ElevatorPointCombination> elevatorPointCombinations) {
        this.elevatorPointCombinations = elevatorPointCombinations;
    }

    public Long getElevatorshaftId() {
        return elevatorshaftId;
    }

    public void setElevatorshaftId(Long elevatorshaftId) {
        this.elevatorshaftId = elevatorshaftId;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getIpElevatorId() {
        return ipElevatorId;
    }

    public void setIpElevatorId(String ipElevatorId) {
        this.ipElevatorId = ipElevatorId;
    }

    public Boolean getDefaultElevator() {
        return defaultElevator;
    }

    public void setDefaultElevator(Boolean defaultElevator) {
        this.defaultElevator = defaultElevator;
    }

    //电梯动作
    public static enum ELEVATOR_ACTION {
        ELEVATOR_LOCK,
        ELEVATOR_UNLOCK
    }

}
