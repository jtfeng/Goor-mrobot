package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

public interface ElevatorShaftService extends BaseService<ElevatorShaft> {

    /**
     * 分页查询所有的电梯井信息
     * @param whereRequest
     * @return
     * @throws Exception
     */
    public List<ElevatorShaft> listElevatorShafts(WhereRequest whereRequest) throws Exception;

}