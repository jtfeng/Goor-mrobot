package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by abel on 17-7-14.
 */
@Service
@Transactional
public class MissionItemTaskServiceImpl
        extends BaseServiceImpl<MissionItemTask>
        implements MissionItemTaskService {
}