package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.muye.util.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface ElevatorPointCombinationMapper extends MyMapper<ElevatorPointCombination> {

    List<ElevatorPointCombination> findByElevatorId(Long elevatorId);

    Long checkCreateCondition(List<Long> mappointIds);

    Long findElevatorByWaitPoint(Long waitPointId);
}