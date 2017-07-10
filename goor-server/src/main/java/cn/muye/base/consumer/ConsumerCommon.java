package cn.muye.base.consumer;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.Date;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);

//    @Autowired
//    private ReceiveMessageService receiveMessageService;

      @Autowired
      private RabbitTemplate rabbitTemplate;

    @Autowired
    private OffLineMessageService offLineMessageService;

    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_PUB)
    public void directAgentPub(@Payload MessageInfo messageInfo) {
        try {
            if(null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())){
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if(!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")){
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
//                else if(){
//
//                }
            }
        }catch (Exception e){
            logger.error("consumer directAgentPub exception",e);
        }
    }

    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_SUB)
    public void directAgentSub(@Payload MessageInfo messageInfo) {
        try {
            if(null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())){
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if(!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")){
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
//                else if(){
//
//                }
            }
        }catch (Exception e){
            logger.error("consumer directAgentSub exception",e);
        }
    }

    @RabbitListener(queues = TopicConstants.DIRECT_APP_PUB)
    public void directAppPub(@Payload MessageInfo messageInfo) {
        try {
            if(null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())){
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if(!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")){
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }

//                else if(){
//
//                }
            }
        }catch (Exception e){
            logger.error("consumer directAppPub exception",e);
        }
    }

    @RabbitListener(queues = TopicConstants.DIRECT_APP_SUB)
    public void directAppSub(@Payload MessageInfo messageInfo) {
        try {
            if(null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())){
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if(!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")){
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
//                else if(){
//
//                }
            }
        }catch (Exception e){
            logger.error("consumer directAppSub exception",e);
        }
    }

    @RabbitListener(queues = TopicConstants.DIRECT_CURRENT_POSE)
    public void directCurrentPose(@Payload MessageInfo messageInfo) {
        try {
            CacheInfoManager.setMessageCache(messageInfo);
        }catch (Exception e){
            logger.error("consumer directCurrentPose exception",e);
        }
    }

    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT)
    public void directCommandReport(@Payload MessageInfo messageInfo) {
        try {
            if (MessageType.TIME_SYNCHRONIZED.equals(messageInfo.getMessageType())) {
                clientTimeSynchronized(messageInfo);
                return;
            }

            sendMessageSave(messageInfo);
        }catch (Exception e){
            logger.error("consumer directCommandReport exception",e);
        }
    }

    /**
     * x86 请求与云端时间同步
     * @param messageInfo
     */
    private void clientTimeSynchronized(MessageInfo messageInfo){
        //监听上行X86时间同步消息，获得X86时间，与服务器时间比较，如果差值大于10s（默认），进行时间同步，否则不处理
        Date date = new Date();
        long upTime = messageInfo.getSendTime().getTime();
        long downTime = date.getTime();
        if ((downTime - upTime) < 10) {
            return;
        } else {
            //发送带响应同步消息，获得10次时间平均延迟
            int sum = 0;

            try {
                //todo:rabbitmq响应超时问题，
                MessageInfo sendMessageInfo = new MessageInfo();
                for (int i = 0; i < 10; i++) {
                    long startTime = System.currentTimeMillis();
                    sendMessageInfo.setMessageType(MessageType.TIME_SYNCHRONIZED);
                    AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);//后期带上机器编码进行区分
                    System.out.println("the delay time :" + result.toString());
                    long endTime = System.currentTimeMillis();
                    sum += (endTime - startTime);
                }

                long avg = sum / 20; //只需要考虑单向时间误差

                //给指定X86发送时间同步消息
                sendMessageInfo.setMessageText(String.valueOf(new Date().getTime() + avg));
                AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);
                System.out.println("the time synchronized result :" + result);
            } catch (Exception e){
                logger.error("time synchronized failure : " +e.toString());
            }
        }
        return;
    }

    private boolean sendMessageSave(MessageInfo messageInfo) throws Exception{
        if(messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUUID() + "")){
            return false;
        }
        if(MessageType.EXECUTOR_LOG.equals(messageInfo.getMessageType())) {
            //TODO 此处可以添加日志及状态上报存储

        }else if(MessageType.REPLY.equals(messageInfo.getMessageType())){
            OffLineMessage message = new OffLineMessage();
            message.setMessageStatusType(messageInfo.getMessageStatusType().getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setUUID(messageInfo.getUUID());//更新的主键
            message.setUpdateTime(new Date());//更新时间
            offLineMessageService.update(message);//更新发送的消息
        }
        return true;
    }

}
