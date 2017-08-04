package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-8-3.
 */
public class JsonElevatorLock implements Serializable {
    private static final long serialVersionUID = 3215650912460993916L;

    String action;
    Long elevator_id;
    Integer result;
    private Long sendTime;
    private String uuid;

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getElevator_id() {
        return elevator_id;
    }

    public void setElevator_id(Long elevator_id) {
        this.elevator_id = elevator_id;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
