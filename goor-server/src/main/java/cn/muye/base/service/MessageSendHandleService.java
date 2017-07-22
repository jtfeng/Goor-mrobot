package cn.muye.base.service;


import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;

public interface MessageSendHandleService {

    //单消息（命令）
    AjaxResult sendCommandMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;
    //单消息（资源）
    AjaxResult sendResourceMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;
    //单消息（x86Agent）
    AjaxResult sendToX86Message(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;
    //命令发送全部机器人
    AjaxResult sendCommandMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;
    //资源发送全部机器人
    AjaxResult sendResourceMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;
    //发送到x86Agent全部机器人
    AjaxResult sendToX86MessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;
}
