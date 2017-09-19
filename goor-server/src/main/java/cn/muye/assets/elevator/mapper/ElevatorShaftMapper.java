package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.muye.util.MyMapper;

public interface ElevatorShaftMapper extends MyMapper<ElevatorShaft> {
    void updateElevatorShaftLockState(Long elevatorId, Integer state);
}