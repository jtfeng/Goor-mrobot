package cn.muye.funcs.service;

import cn.mrobot.bean.order.Order;

/**
 * Created by abel on 17-7-13.
 */
public interface MissionFuncsService {

    /**
     * 根据订单数据创建任务列表
     * @param order
     * @return
     */
    boolean createMissionLists(Order order);
}
