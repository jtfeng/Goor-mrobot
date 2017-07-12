package cn.muye.base.consumer.service;

import cn.muye.base.bean.MessageInfo;

/**
 * Created by abel on 17-7-11.
 */
public interface BaseMessageService {

    /**
     * switch (baseMessageService.getMessageName(messageInfo)){
     case TopicConstants.PICK_UP_PSWD_VERIFY:
     //收到云端服务器返回的结果，处理

     break;
     default:
     break;
     }
     */

    String getPubData(MessageInfo messageInfo);
    String getMessageName(MessageInfo messageInfo);
    String getSenderId(MessageInfo messageInfo);
    void sendCloudMessage(String pubName, Object data);
}
