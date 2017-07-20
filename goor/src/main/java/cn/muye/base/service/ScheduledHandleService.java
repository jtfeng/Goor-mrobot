package cn.muye.base.service;


import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import edu.wpi.rail.jrosbridge.Ros;

public interface ScheduledHandleService {

    void receiveMessage();

    void rosHealthCheck();

    void downloadResource();

    AjaxResult downloadResource(Ros ros, MessageInfo messageInfo);

    void publishMessage();

    AjaxResult publishMessage(Ros ros, MessageInfo messageInfo);

    void executeTwentyThreeAtNightPerDay();

    void timeSynchronized(String localRobotSN);

    void sendRobotInfo() throws Exception;
}
