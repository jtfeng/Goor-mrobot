package cn.muye.log.mission.service.impl;

import cn.mrobot.bean.log.mission.LogMission;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.log.mission.service.LogMissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
@Service
@Transactional
public class LogMissionServiceImpl extends BaseServiceImpl<LogMission> implements LogMissionService {
}
