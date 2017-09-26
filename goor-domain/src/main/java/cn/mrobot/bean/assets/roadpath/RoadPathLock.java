package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.base.BaseBean;
import javax.persistence.Table;

@Table(name = "AS_ROADPATHLOCK")
public class RoadPathLock extends BaseBean{

    private String name;
    private Integer lockState;
    private String robotCode;

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

    @Override
    public String toString() {
        return "RoadPathLock{" +
                "name='" + name + '\'' +
                ", lockState=" + lockState +
                ", robotCode=" + robotCode +
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