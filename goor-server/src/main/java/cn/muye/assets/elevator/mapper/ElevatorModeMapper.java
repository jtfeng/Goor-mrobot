package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorMode;
import cn.muye.util.MyMapper;

import java.util.List;

public interface ElevatorModeMapper extends MyMapper<ElevatorMode> {

    Long checkLegalRangeDate(String startTime, String endTime, Long elevatorId);

    List<ElevatorMode> listElevatorModesByElevatorId(Long elevatorId);

}