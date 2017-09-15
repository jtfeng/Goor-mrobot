package cn.mrobot.bean.log.alert;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
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

    private String description;

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
}
