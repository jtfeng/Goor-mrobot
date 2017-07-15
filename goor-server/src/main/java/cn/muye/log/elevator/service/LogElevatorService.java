package cn.muye.log.elevator.service;

import cn.mrobot.bean.log.elevator.LogElevator;
import cn.mrobot.bean.log.mission.LogMission;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface LogElevatorService extends BaseService<LogElevator> {
    List<LogElevator> listPageByTimeDesc(int page, int pageSize);
}
