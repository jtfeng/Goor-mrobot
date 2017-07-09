package cn.muye.base.service;


import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;

public interface ScheduledHandleService {

    void receiveMessage();

    void rosHealthCheck();

    void downloadResource();

    AjaxResult downloadResource(Ros ros, MessageInfo messageInfo);

    void publishMessage();

    AjaxResult publishMessage(Ros ros, MessageInfo messageInfo);

    void executeTwentyThreeAtNightPerDay();

}
