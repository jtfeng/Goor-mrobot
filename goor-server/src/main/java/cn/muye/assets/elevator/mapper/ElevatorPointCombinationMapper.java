package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.muye.util.MyMapper;

import java.util.List;

public interface ElevatorPointCombinationMapper extends MyMapper<ElevatorPointCombination> {

    List<ElevatorPointCombination> findByElevatorId(Long elevatorId);

    Long checkCreateCondition(List<Long> mappointIds);

}