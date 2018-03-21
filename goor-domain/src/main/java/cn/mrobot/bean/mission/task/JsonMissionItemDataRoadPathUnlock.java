package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataRoadPathUnlock implements Serializable {
    private static final long serialVersionUID = 1L;

    //路径锁ID
    private Long roadpath_id;
    //间隔时间
    private Integer interval_time;
    //工控路径ID---->20180321在加解锁任务中改成云端主键ID，因为工控路径ID不再全局唯一。
    private Long x86_path_id;

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

    public Long getX86_path_id() {
        return x86_path_id;
    }

    public void setX86_path_id(Long x86_path_id) {
        this.x86_path_id = x86_path_id;
    }
}
