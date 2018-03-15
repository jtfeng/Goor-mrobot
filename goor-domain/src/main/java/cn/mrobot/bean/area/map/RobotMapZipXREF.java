package cn.mrobot.bean.area.map;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import java.util.Date;

/**
 *
 * @author Jelynn
 * @date 2017/8/15
 * 云端场景，机器人，地图压缩包关联关系
 */
@Table(name = "ROBOT_MAPZIP_XREF")
public class RobotMapZipXREF {

    private Long robotId;

    private Long mapZipId;

    private Long sceneId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private boolean success;

    private int deleteFlag;   //数据库删除状态 0 :正常 1：删除

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;   //数据库删除时间

    public static class Builder {

        private Long robotId;
        private Long mapZipId;
        private Long sceneId;
        private Date updateTime;
        private boolean success;
        private int deleteFlag;
        private Date deleteTime;

        public Builder robotId(Long robotId) {
            this.robotId = robotId;
            return this;
        }

        public Builder mapZipId(Long mapZipId) {
            this.mapZipId = mapZipId;
            return this;
        }

        public Builder sceneId(Long sceneId) {
            this.sceneId = sceneId;
            return this;
        }

        public Builder updateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder deleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
            return this;
        }

        public Builder deleteTime(Date deleteTime) {
            this.deleteTime = deleteTime;
            return this;
        }

        public RobotMapZipXREF build() {
            return new RobotMapZipXREF(this);
        }
    }

    public RobotMapZipXREF() {
    }

    public RobotMapZipXREF(Builder builder) {
        this.robotId = builder.robotId;
        this.mapZipId = builder.mapZipId;
        this.sceneId = builder.sceneId;
        this.updateTime = builder.updateTime;
        this.success = builder.success;
        this.deleteFlag = builder.deleteFlag;
        this.deleteTime = builder.deleteTime;
    }

    public Long getRobotId() {
        return robotId;
    }

    public Long getMapZipId() {
        return mapZipId;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
