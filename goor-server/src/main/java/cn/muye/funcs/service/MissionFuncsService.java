package cn.muye.funcs.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Order;

import java.util.List;

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

    /**
     * 获取任务下发的消息对象json字串
     * @param listTasks
     * @return
     */
    String getGoorMissionMsg(List<MissionListTask> listTasks);
}
