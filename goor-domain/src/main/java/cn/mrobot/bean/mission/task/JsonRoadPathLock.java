package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-8-3.
 */
public class JsonRoadPathLock implements Serializable {
    private static final long serialVersionUID = 3215650912460993916L;

    //加锁还是解锁
    String action;
    //路径锁的ID
    Long roadpath_id;
    //加锁结果
    Integer result;
    private Long sendTime;
    private String uuid;

    //工控路径ID
    Long x86_path_id;

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

    public Long getRoadpath_id() {
        return roadpath_id;
    }

    public void setRoadpath_id(Long roadpath_id) {
        this.roadpath_id = roadpath_id;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Long getX86_path_id() {
        return x86_path_id;
    }

    public void setX86_path_id(Long x86_path_id) {
        this.x86_path_id = x86_path_id;
    }
}
