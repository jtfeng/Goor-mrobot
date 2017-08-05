package cn.muye.base.service;


import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import edu.wpi.rail.jrosbridge.Ros;

public interface ScheduledHandleService {

    void receiveMessage() throws Exception;

    void rosHealthCheck() throws Exception;

    void mqHealthCheck(String queueName) throws Exception;

    void downloadResource() throws Exception;

    AjaxResult downloadResource(Ros ros, MessageInfo messageInfo) throws Exception;

    void publishMessage() throws Exception;

    AjaxResult publishMessage(Ros ros, MessageInfo messageInfo) throws Exception;

    void executeTwentyThreeAtNightPerDay() throws Exception;

    void timeSynchronized(String localRobotSN) throws Exception;

    void sendRobotInfo() throws Exception;
}
