package cn.muye.log.base.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class RobotLogWarningDetail {

    @JSONField(format = "HH:mm")
    private Date dateTime;

    private String type;

    private int time = 1; //暂时默认为1

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}