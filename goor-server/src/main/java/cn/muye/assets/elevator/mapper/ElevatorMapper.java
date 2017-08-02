package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.muye.util.MyMapper;

import java.util.List;

public interface ElevatorMapper extends MyMapper<Elevator> {

    List<Elevator> findByElevatorPointCombinationId(Long elevatorPointCombinationId);

    void deleteRelationsByElevatorId(Long id);

    void insertRelationsByElevatorId(Long id, List<Long> combinationIds);
}