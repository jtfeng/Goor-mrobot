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

}