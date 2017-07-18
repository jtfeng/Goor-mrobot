package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionTask;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface MissionTaskService extends BaseService<MissionTask> {
    List<MissionTask> findByListId(Long listId);
}
