package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;

public class RabbitMqBean {

    /**
     * 获取需要发送的RoutingKey，当有新的发送任务，不包含在以下的，添加时以下也需要添加
     * @param robotSN  机器序列号
     * @param isReceive 是否需要返回值
     * @param messageInfo 发送消息MessageInfo，注意设置messageInfo.setMessageType()
     * @return 返回RoutingKey
     */
    public static String getRoutingKey(String robotSN, boolean isReceive, MessageInfo messageInfo){
        if(MessageType.EXECUTOR_COMMAND.equals(messageInfo.getMessageType())){
            if(isReceive){
                return TopicConstants.TOPIC_COMMAND_RECEIVE_ROUTING_KEY+robotSN;
            }else{
                return TopicConstants.TOPIC_COMMAND_ROUTING_KEY+robotSN;
            }
        }
        if(MessageType.EXECUTOR_RESOURCE.equals(messageInfo.getMessageType())
                || MessageType.EXECUTOR_UPGRADE.equals(messageInfo.getMessageType())){
            if(isReceive){
                return TopicConstants.TOPIC_COMMAND_RECEIVE_ROUTING_KEY+robotSN;
            }else{
                return TopicConstants.TOPIC_COMMAND_ROUTING_KEY+robotSN;
            }
        }
        return "";
    }
}
