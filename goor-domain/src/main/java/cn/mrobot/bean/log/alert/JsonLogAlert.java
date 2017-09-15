package cn.mrobot.bean.log.alert;

import java.io.Serializable;

/**
 * Created by ray.fu on 2017/9/11.
 */
public class JsonLogAlert implements Serializable {
    private static final long serialVersionUID = 4584596429309029960L;

    private Long alert_time;
    private Integer alert_code;
    private String msg;
    private Long mission_item_id;

    public Long getAlert_time() {
        return alert_time;
    }

    public void setAlert_time(Long alert_time) {
        this.alert_time = alert_time;
    }

    public Integer getAlert_code() {
        return alert_code;
    }

    public void setAlert_code(Integer alert_code) {
        this.alert_code = alert_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getMission_item_id() {
        return mission_item_id;
    }

    public void setMission_item_id(Long mission_item_id) {
        this.mission_item_id = mission_item_id;
    }
}
