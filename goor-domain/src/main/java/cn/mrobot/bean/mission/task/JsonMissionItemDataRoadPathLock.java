package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataRoadPathLock implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roadpath_id;
    private Integer interval_time;

    public Integer getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(Integer interval_time) {
        this.interval_time = interval_time;
    }

    public Long getRoadpath_id() {
        return roadpath_id;
    }

    public void setRoadpath_id(Long roadpath_id) {
        this.roadpath_id = roadpath_id;
    }
}
