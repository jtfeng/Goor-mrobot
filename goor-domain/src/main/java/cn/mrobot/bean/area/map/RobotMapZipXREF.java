package cn.mrobot.bean.area.map;

import javax.persistence.Table;

/**
 * Created by Jelynn on 2017/8/15.
 * 云端场景，机器人，地图压缩包关联关系
 */
@Table(name = "ROBOT_MAPZIP_XREF")
public class RobotMapZipXREF {

    private Long robotId;

    private Long lastMapZipId;

    private Long newMapZipId;

    private boolean success;

    public static class Builder {
        private Long robotId;
        private Long lastMapZipId;
        private Long newMapZipId;
        private boolean success;

        public Builder robotId(Long robotId) {
            this.robotId = robotId;
            return this;
        }

        public Builder lastMapZipId(Long lastMapZipId) {
            this.lastMapZipId = lastMapZipId;
            return this;
        }

        public Builder newMapZipId(Long newMapZipId) {
            this.newMapZipId = newMapZipId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public RobotMapZipXREF build() {
            return new RobotMapZipXREF(this);
        }
    }

    public RobotMapZipXREF() {
    }

    public RobotMapZipXREF(Builder builder) {
        this.lastMapZipId = builder.lastMapZipId;
        this.newMapZipId = builder.newMapZipId;
        this.robotId = builder.robotId;
        this.success = builder.success;
    }

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public Long getLastMapZipId() {
        return lastMapZipId;
    }

    public void setLastMapZipId(Long lastMapZipId) {
        this.lastMapZipId = lastMapZipId;
    }

    public Long getNewMapZipId() {
        return newMapZipId;
    }

    public void setNewMapZipId(Long newMapZipId) {
        this.newMapZipId = newMapZipId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
