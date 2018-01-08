package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.muye.util.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ElevatorMapper extends MyMapper<Elevator> {

    List<Elevator> findByElevatorPointCombinationId(Long elevatorPointCombinationId);

    void deleteRelationsByElevatorId(Long id);

    void insertRelationsByElevatorId(Long id, List<Long> combinationIds);

    void updateElevatorLockState(Long elevatorId, Integer state);

    List<MapInfo> findByMapNameAndStoreId(String mapName, Long storeId, String sceneName);

    List<Elevator> selectAllFlushCache();

    /**
     * 根据电台站 ID 查询所绑定的所有电梯
     * @param elevatorStationId
     * @return
     */
    List<Elevator> findByElevatorStationId(Long elevatorStationId);

    /**
     * 查询所属指定场景的所有电梯信息
     * @param id
     * @return
     */
    List<Elevator> listElevatorByScene(Long id);

}