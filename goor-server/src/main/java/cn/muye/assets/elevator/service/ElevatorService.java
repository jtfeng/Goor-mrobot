package cn.muye.assets.elevator.service;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;
import com.google.common.collect.Lists;

import java.util.List;

public interface ElevatorService extends BaseService<Elevator> {

    /**
     * 分页查询所有的电梯信息
     * @param whereRequest
     * @return
     * @throws Exception
     */
    public List<Elevator> listElevators(WhereRequest whereRequest) throws Exception;


    List<Elevator> findByElevatorPointCombinationId(Long elevatorPointCombinationId);

    /**
     * 创建电梯信息
     * @param elevator
     * @throws Exception
     */
    void createElevator(Elevator elevator, List<Long> combinationIds) throws Exception;

    /**
     * 更新电梯信息
     * @param elevator
     * @throws Exception
     */
    void updateElevator(Elevator elevator, List<Long> combinationIds) throws Exception;

    /**
     * 更新电梯状态
     * @param elevatorId
     */
    boolean updateElevatorLockState(Long elevatorId, Elevator.ELEVATOR_ACTION action);

    /**
     * 根据地图楼层查询电梯信息（ Abel 使用）
     * @param mapInfoId
     * @param floor
     * @return
     */
    List<Elevator> findByMapFloor(Long mapInfoId, Integer floor) throws Exception;

    /**
     * 根据地图名称以及门店编号查询对应的地图实体信息
     * @param mapName
     * @param storeId
     * @return
     */
     MapInfo findByMapNameAndStoreId(String mapName, Long storeId) throws Exception;
}