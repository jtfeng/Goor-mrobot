package cn.muye.base.consumer.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-7-15.
 */
@Service
public class X86MissionDispatchServiceImpl implements X86MissionDispatchService {

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void sendX86MissionDispatch(
            String robotCode,
            MissionListTask missionListTask) {

    }
}
