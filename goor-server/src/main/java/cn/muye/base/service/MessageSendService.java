package cn.muye.base.service;


import cn.muye.base.bean.MessageInfo;

public interface MessageSendService {

    Integer sendMessage(String toUserId, MessageInfo messageInfo);

    Integer sendReplyMessage(String toUserId, MessageInfo messageInfo);

    Integer sendNoStatusMessage(String toUserId, MessageInfo messageInfo);

}
