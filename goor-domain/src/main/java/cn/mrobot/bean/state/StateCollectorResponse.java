package cn.mrobot.bean.state;

import java.util.Date;

/**
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorResponse {

    private String state;

    private int type;

    private String description;

    private String module;

    private Date time;

    private String senderId;//发送ID或机器序列号

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
