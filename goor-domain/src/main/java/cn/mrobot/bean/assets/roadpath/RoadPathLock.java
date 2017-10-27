package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.base.BaseBean;
import javax.persistence.Table;

@Table(name = "AS_ROADPATHLOCK")
public class RoadPathLock extends BaseBean{

    private String name;
    private Integer lockState;
    private String robotCode;
    private Long passCount;
    private Long currentPasscount;
    private String robotCodes;
    private Long direction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLockState() {
        return lockState;
    }

    public void setLockState(Integer lockState) {
        this.lockState = lockState;
    }

    public void setLockAction(LockAction action) {
        setLockState(action.getValue());
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Long getPassCount() {
        return passCount;
    }

    public void setPassCount(Long passCount) {
        this.passCount = passCount;
    }

    public Long getCurrentPasscount() {
        return currentPasscount;
    }

    public void setCurrentPasscount(Long currentPasscount) {
        this.currentPasscount = currentPasscount;
    }

    public String getRobotCodes() {
        return robotCodes;
    }

    public void setRobotCodes(String robotCodes) {
        this.robotCodes = robotCodes;
    }

    public Long getDirection() {
        return direction;
    }

    public void setDirection(Long direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "RoadPathLock{" +
                "name='" + name + '\'' +
                ", lockState=" + lockState +
                ", robotCode='" + robotCode + '\'' +
                ", passCount=" + passCount +
                ", currentPasscount=" + currentPasscount +
                ", robotCodes='" + robotCodes + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }

    public static enum LockAction {
        LOCK(1), UNLOCK(0);
        private Integer value;
        LockAction(Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return value;
        }
        public void setValue(Integer value) {
            this.value = value;
        }
    }
}