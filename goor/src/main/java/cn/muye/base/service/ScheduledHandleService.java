package cn.muye.base.service;


import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import edu.wpi.rail.jrosbridge.Ros;

public interface ScheduledHandleService {

    void receiveMessage() throws Exception;

    void rosHealthCheck() throws Exception;

    void x86MissionRosHealthCheck() throws Exception;

    void mqHealthCheck(String queueName) throws Exception;

    void downloadResource() throws Exception;

    AjaxResult downloadResource(Ros ros, MessageInfo messageInfo) throws Exception;

    void publishMessage() throws Exception;

    AjaxResult publishMessage(Ros ros, MessageInfo messageInfo) throws Exception;

    void executeTwentyThreeAtNightPerDay() throws Exception;

    void timeSynchronized(String localRobotSN) throws Exception;

    void sendRobotInfo() throws Exception;

    /**
     * 定时任务接口，每30秒检查一次
     * @param uuid
     * @throws Exception
     */
    void robotOnlineState(String uuid) throws Exception;

    /**
     * app主动查询接口，将查询结果按照2秒每次发送10，收到反馈后停止发送
     * @param uuid
     * @throws Exception
     */
    void robotOnlineStateQuery(String uuid) throws Exception;
}
