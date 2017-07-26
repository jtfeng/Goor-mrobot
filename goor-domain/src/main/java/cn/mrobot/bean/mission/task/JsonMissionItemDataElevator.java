package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataElevator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer floor;

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
}
