package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

public interface ElevatorPointCombinationService extends BaseService<ElevatorPointCombination> {

    /**
     * 查询所有的点组合信息（表示一个整体）
     * @param whereRequest
     * @return
     * @throws Exception
     */
    public List< ElevatorPointCombination> listElevatorPointCombinations(WhereRequest whereRequest) throws Exception;

    /**
     * 根据电梯编号获取对应的四点组合信息
     * @param elevatorId
     * @return
     */
    public List<ElevatorPointCombination> findByElevatorId(Long elevatorId) throws Exception;

    /**
     * 检查所传入的地图点是否属于同一张地图
     * @param mappointIds
     * @return
     */
    boolean checkCreateCondition(List<Long> mappointIds) throws Exception;
}