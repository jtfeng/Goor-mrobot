package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface MissionItemTaskService extends BaseService<MissionItemTask> {
    List<MissionItemTask> findByListIdAndMissionId(Long listId, Long missionId);
}
