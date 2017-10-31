package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorMode;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

public interface ElevatorModeService extends BaseService<ElevatorMode> {

    List<ElevatorMode> listElevatorModesByElevatorId(Long elevatorId) throws Exception;

}