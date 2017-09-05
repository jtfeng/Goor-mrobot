package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.base.BaseBean;
import javax.persistence.Table;

@Table(name = "AS_ROADPATHLOCK")
public class RoadPathLock extends BaseBean{

    private String name;
    private Integer lock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLock() {
        return lock;
    }

    private void setLock(Integer lock) {
        this.lock = lock;
    }

    public void setLockAction(LockAction action) {
        setLock(action.getValue());
    }

    @Override
    public String toString() {
        return "RoadPathLock{" +
                "name='" + name + '\'' +
                ", lock=" + lock +
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