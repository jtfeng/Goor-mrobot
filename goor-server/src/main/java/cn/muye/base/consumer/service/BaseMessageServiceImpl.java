package cn.muye.base.consumer.service;

import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by abel on 17-7-11.
 */
@Service
public class BaseMessageServiceImpl implements BaseMessageService {

    private Logger logger = Logger.getLogger(BaseMessageServiceImpl.class);

    @Autowired
    private MsgSendAsyncTask msgSendAsyncTask;

    /**
     * 获取消息里的data
     * @param messageInfo
     * @return
     */
    @Override
    public String getPubData(MessageInfo messageInfo){
        JSONObject requestDataObject =
                JSON.parseObject(messageInfo.getMessageText());
        if (requestDataObject == null){
            return "";
        }
        String ret = null;
        try {
            ret = requestDataObject.getString(TopicConstants.DATA);
            JSONObject jsonObjectData = JSON.parseObject(ret);
            if (jsonObjectData != null){
                ret = jsonObjectData.getString(TopicConstants.DATA);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return ret;
    }

    /**
     * 获取消息里的MessageName
     * @param messageInfo
     * @return
     */
    @Override
    public String getMessageName(MessageInfo messageInfo){
        if (messageInfo == null){
            return "";
        }

        JSONObject requestDataObject =
                JSON.parseObject(messageInfo.getMessageText());
        if (requestDataObject == null){
            return "";
        }

        JSONObject jsonObjectData = JSON.parseObject(
                requestDataObject.getString(TopicConstants.DATA));

        String ret = "";
        try {
            ret = jsonObjectData.getString(TopicConstants.PUB_NAME);
        } catch (Exception e) {
            logger.error(e);
        }

        return ret;
    }

    /**
     * 获取消息里的senderId
     * @param messageInfo
     * @return
     */
    @Override
    public String getSenderId(MessageInfo messageInfo){
        if (messageInfo == null ||
                StringUtil.isEmpty(messageInfo.getSenderId())){
            return "";
        }

        return messageInfo.getSenderId();
    }

    /**
     * 给指定机器人发送消息
     * @param robotCode
     * @param slamResponseBody
     */
    @Override
    public void sendRobotMessage(String robotCode, SlamResponseBody slamResponseBody){
        if (slamResponseBody == null){
            return;
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(slamResponseBody));

        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_SUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(messageObject.toJSONString());
        //如：
//        commonInfo.setPublishMessage(TopicConstants.GET_CURRENT_MAP_PUB_MESSAGE);

        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotCode);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
//        info.setMessageType(MessageType.EXECUTOR_RESOURCE);//TODO 如果发送资源,将此行注释去掉，注释此行上面第一行
//        info.setMessageType(MessageType.EXECUTOR_LOG);//TODO 针对 x86 agent 业务逻辑,不接收发送到ros的信息，如：发送命令要求上传log等
        info.setMessageText(JSON.toJSONString(commonInfo));//TODO 发送资源及rostopic命令

        //获取当前需要发送的的routingKey,其中"SNabc001"为机器人SN号
//        String noResultCommandRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_COMMAND.name());
        String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey(
                robotCode,true, MessageType.EXECUTOR_COMMAND.name());

//        String noResultResourceRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_RESOURCE.name());
//        String backResultResourceRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",true, MessageType.EXECUTOR_RESOURCE.name());
//
//        String noResultClientRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_CLIENT.name());
//        String backResultClientRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",true, MessageType.EXECUTOR_CLIENT.name());

        //单机器命令发送（不带回执）
//        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultCommandRoutingKey, info);

        //单机器命令发送（带回执）
        try {
            msgSendAsyncTask.taskMsgSend(backResultCommandRoutingKey, info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //全部机器命令发送
//        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_COMMAND_EXCHANGE, "", info);

        //单机器资源发送（不带回执）
//        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultResourceRoutingKey, info);

        //单机器资源发送（带回执）
//        AjaxResult ajaxResourceResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultResourceRoutingKey, info);

        //全部机器资源发送
//        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_RESOURCE_EXCHANGE, "", info);

        //单机器发送，仅供x86 agent 处理业务逻辑，不发ros消息（不带回执）
//        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultClientRoutingKey, info);

        //单机器发送，仅供x86 agent 处理业务逻辑，不发ros消息（带回执）
//        AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, info);

        //全部机器x86 agent发送,仅供x86 agent 处理业务逻辑，不发ros消息
//        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_RESOURCE_EXCHANGE, "", info);

    }

}
