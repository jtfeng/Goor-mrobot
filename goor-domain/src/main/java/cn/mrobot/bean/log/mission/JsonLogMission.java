package cn.mrobot.bean.log.mission;

import java.io.Serializable;

/**
 * Created by abel on 17-7-17.
 */
public class JsonLogMission implements Serializable {
    private static final long serialVersionUID = 4584596429309029960L;

    private String object;
    private Long mission_list_id;
    private Long mission_id;
    private Long mission_item_id;
    private Integer mission_list_repeat_times;
    private Integer mission_repeat_times;
    private String event;
    private String description;
    private String mission_item_name;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getMission_list_id() {
        return mission_list_id;
    }

    public void setMission_list_id(Long mission_list_id) {
        this.mission_list_id = mission_list_id;
    }

    public Long getMission_id() {
        return mission_id;
    }

    public void setMission_id(Long mission_id) {
        this.mission_id = mission_id;
    }

    public Long getMission_item_id() {
        return mission_item_id;
    }

    public void setMission_item_id(Long mission_item_id) {
        this.mission_item_id = mission_item_id;
    }

    public Integer getMission_list_repeat_times() {
        return mission_list_repeat_times;
    }

    public void setMission_list_repeat_times(Integer mission_list_repeat_times) {
        this.mission_list_repeat_times = mission_list_repeat_times;
    }

    public Integer getMission_repeat_times() {
        return mission_repeat_times;
    }

    public void setMission_repeat_times(Integer mission_repeat_times) {
        this.mission_repeat_times = mission_repeat_times;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMission_item_name() {
        return mission_item_name;
    }

    public void setMission_item_name(String mission_item_name) {
        this.mission_item_name = mission_item_name;
    }
}
