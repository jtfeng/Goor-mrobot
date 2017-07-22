package cn.muye.base.service;


import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;

public interface MessageSendHandleService {

    AjaxResult sendCommandMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;

    AjaxResult sendResourceMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;

    AjaxResult sendToX86Message(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception;

    AjaxResult sendCommandMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;

    AjaxResult sendResourceMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;

    AjaxResult sendToX86MessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception;
}
