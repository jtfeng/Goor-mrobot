package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface MissionListTaskService extends BaseService<MissionListTask> {
    List<MissionListTask> findByRobotCodeAndState(
            String robotCode,
            String state);
}
