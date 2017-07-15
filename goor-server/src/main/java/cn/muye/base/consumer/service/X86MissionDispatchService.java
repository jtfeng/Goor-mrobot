package cn.muye.base.consumer.service;

import cn.mrobot.bean.mission.task.MissionListTask;
/**
 * Created by abel on 17-7-11.
 */
public interface X86MissionDispatchService {

    void sendX86MissionDispatch(
            String robotCode,
            MissionListTask missionListTask);
}
