package cn.muye.service.missiontask;

import cn.mrobot.bean.mission.MissionList;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
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

    /**
     * 获取指定机器人当前执行任务状态的列表，如果没有执行中的任务，则返回null
     * @param robotCode
     * @return
     */
    List<MissionTask> getMissionTaskStatus(String robotCode);

    /**
     * 根据MissionList列表和机器人列表生成MissionListTask列表并发送到机器人
     * @param robotCodesArray
     * @param missionLists
     * @param name
     * @return
     */
    Boolean createMissionListTasksByMissionLists(String[] robotCodesArray, List<MissionList> missionLists)  throws Exception;


}
