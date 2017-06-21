package cn.muye.base.service;


import cn.muye.base.bean.MessageInfo;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;

public interface ScheduledHandleService {

    void sendMessage();

    void receiveMessage();

    void rosHealthCheck();

    void downloadResource();

    void downloadResource(Ros ros, Client client, MessageInfo messageInfo);

    void publishMessage();

    void publishMessage(Ros ros, Client client, MessageInfo messageInfo);

    void executeTwentyThreeAtNightPerDay();

}
