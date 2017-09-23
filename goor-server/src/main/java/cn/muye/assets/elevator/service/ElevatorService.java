package cn.muye.assets.elevator.service;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorModeEnum;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

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
     * 更新电梯状态
     * @param elevatorId
     */
    boolean updateElevatorLockStateWithRobotCode(Long elevatorId, Elevator.ELEVATOR_ACTION action, String robotCode);

    /**
     * 根据地图楼层查询电梯信息（ Abel 使用）
     * @param mapInfoId
     * @param floor
     * @return
     */
    List<Elevator> findByMapFloor(Long mapInfoId, Integer floor);

    /**
     * 根据地图楼层查询电梯信息（ Abel 使用，外加一个等待点 MapPoint 的参数，将查询结果限制在对应的电梯井）
     * @param mapInfoId
     * @param floor
     * @return
     */
    List<Elevator> findByMapFloor(Long mapInfoId, Integer floor, MapPoint waitPoint);

    /**
     * 根据地图名称以及门店编号查询对应的地图实体信息
     * @param mapName
     * @param storeId
     * @return
     */
     MapInfo findByMapNameAndStoreId(String mapName, Long storeId, String sceneName) throws Exception;

    /**
     * 判断电梯在当前时刻的模式
     * @return  电梯模式，枚举
     * @throws Exception    程序出现异常
     */
    ElevatorModeEnum determineCurrentElevatorMode(Long elevatorId) throws Exception;

}