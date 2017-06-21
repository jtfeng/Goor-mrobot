package cn.muye.service;


import cn.muye.bean.MessageInfo;
import com.mpush.api.Client;

public interface MessageHandleService {

    //命令消息
    void executorCommandMessage(Client client, MessageInfo messageInfo);

    //log消息
    void executorLogMessage(Client client, MessageInfo messageInfo);

    //资源消息
    void executorResourceMessage(Client client, MessageInfo messageInfo);

    //升级消息
    void executorUpgradeMessage(Client client, MessageInfo messageInfo);

    //回执消息
    void replyMessage(Client client, MessageInfo messageInfo);

    //...更多消息自行定义

}
