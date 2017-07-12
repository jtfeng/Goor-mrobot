package cn.muye.base.consumer.service;

import cn.mrobot.bean.slam.SlamResponseBody;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by abel on 17-7-11.
 */
public interface BaseMessageService {

    String getPubData(MessageInfo messageInfo);
    String getMessageName(MessageInfo messageInfo);
    String getSenderId(MessageInfo messageInfo);
    void sendRobotMessage(String robotCode, SlamResponseBody slamResponseBody);
}
