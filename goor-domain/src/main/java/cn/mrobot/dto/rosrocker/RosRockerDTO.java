package cn.mrobot.dto.rosrocker;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by wlkfec on 17/07/2017.
 */

/**
 * ros 摇杆需要发送的消息
 */
public class RosRockerDTO implements Serializable {

    @JSONField(serialize = false)
    private String robotId;
    private Linear linear;
    private Angular angular;

    public RosRockerDTO() {
    }

    public RosRockerDTO(String robotId, Linear linear, Angular angular) {
        this.robotId = robotId;
        this.linear = linear;
        this.angular = angular;
    }

    public static class Linear implements Serializable{
        private Double x;
        private Double y;
        private Double z;

        public Linear() {
        }

        public Linear(Double x, Double y, Double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }

        public Double getZ() {
            return z;
        }

        public void setZ(Double z) {
            this.z = z;
        }
    }

    public static class Angular implements Serializable{
        private Double x;
        private Double y;
        private Double z;

        public Angular() {
        }

        public Angular(Double x, Double y, Double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }

        public Double getZ() {
            return z;
        }

        public void setZ(Double z) {
            this.z = z;
        }
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public Linear getLinear() {
        return linear;
    }

    public void setLinear(Linear linear) {
        this.linear = linear;
    }

    public Angular getAngular() {
        return angular;
    }

    public void setAngular(Angular angular) {
        this.angular = angular;
    }
}
