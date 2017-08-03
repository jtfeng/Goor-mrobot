package cn.muye.assets.elevator.service;

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
     * @param state
     */
    void updateElevatorLockState(Long elevatorId, Integer state);

    /**
     * 根据地图楼层查询电梯信息
     * todo 必须返回的是电梯当层的点集合
     * @param mapInfoId
     * @param floor
     * @return
     */
    List<Elevator> findByMapFloor(Long mapInfoId, Integer floor);
}