package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by abel on 17-7-11.
 */
public interface BaseMessageService {

    String getData(MessageInfo messageInfo);
    String getPubData(MessageInfo messageInfo);
    String getMessageName(MessageInfo messageInfo);
    String getSenderId(MessageInfo messageInfo);
    void sendRobotMessage(String robotCode, SlamResponseBody slamResponseBody);
    void sendRobotMessage(String robotCode, String topic, SlamResponseBody slamResponseBody);
    void sendRobotMessage(String robotCode, String data);
    AjaxResult sendRobotMessage(String robotCode, String topic, String data);
    void sendAllRobotMessage(String data);
    void sendAllRobotMessage(String topic, String data);
}
