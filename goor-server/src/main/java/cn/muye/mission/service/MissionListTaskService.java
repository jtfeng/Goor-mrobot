package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface MissionListTaskService extends BaseService<MissionListTask> {
    List<MissionListTask> findByRobotCodeAndState(
            String robotCode,
            String state);

    MissionListTask findLastByRobotCode(
            String robotCode);

    List<MissionListTask> listPageByStoreIdAndOrder(
            int page,
            int pageSize,
            Long sceneId,
            String state,
            String order);

    List<MissionListTask> tasksList(WhereRequest whereRequest);

    List<MissionListTask> findTodayList();

    List<MissionListTask> findByOrderIds(List<Long> processingOrderIdsToday);

    MissionListTask findByOrderId(Long orderId);
}
