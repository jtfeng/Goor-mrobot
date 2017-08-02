package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
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

}