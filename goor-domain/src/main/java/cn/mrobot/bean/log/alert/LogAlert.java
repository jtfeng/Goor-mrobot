package cn.mrobot.bean.log.alert;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by ray.fu on 2017/9/11.
 */
@Table(name = "LOG_ALERT")
public class LogAlert extends BaseBean {

    private String robotCode;

    private String alertCode;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date alertTime;

    private Long missionItemId;

    @Transient
    private String missionItemDescription;

    private String description;

    @Transient
    private String alertName;

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getAlertCode() {
        return alertCode;
    }

    public void setAlertCode(String alertCode) {
        this.alertCode = alertCode;
    }

    public Date getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Date alertTime) {
        this.alertTime = alertTime;
    }

    public Long getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Long missionItemId) {
        this.missionItemId = missionItemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getMissionItemDescription() {
        return missionItemDescription;
    }

    public void setMissionItemDescription(String missionItemDescription) {
        this.missionItemDescription = missionItemDescription;
    }
}
