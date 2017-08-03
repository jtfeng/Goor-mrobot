package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataElevatorUnlock implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long elevator_id;

    public Long getElevator_id() {
        return elevator_id;
    }

    public void setElevator_id(Long elevator_id) {
        this.elevator_id = elevator_id;
    }
}
