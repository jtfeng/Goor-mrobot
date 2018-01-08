package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorMode;
import cn.muye.util.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ElevatorModeMapper extends MyMapper<ElevatorMode> {

    Long checkLegalRangeDate(String startTime, String endTime, Long elevatorId);

    List<ElevatorMode> listElevatorModesByElevatorId(Long elevatorId);

}