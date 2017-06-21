package cn.muye.base.service;


import cn.muye.base.bean.MessageInfo;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;

public interface MessageHandleService {

    //命令消息
    void executorCommandMessage(Ros ros, Client client, MessageInfo messageInfo);

    //log消息
    void executorLogMessage(Ros ros, Client client, MessageInfo messageInfo);

    //资源消息
    void executorResourceMessage(Ros ros, Client client, MessageInfo messageInfo);

    //升级消息
    void executorUpgradeMessage(Ros ros, Client client, MessageInfo messageInfo);

    //回执消息
    void replyMessage(Ros ros, Client client, MessageInfo messageInfo);

    //...更多消息自行定义

}
