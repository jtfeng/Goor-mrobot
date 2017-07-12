package cn.muye.base.consumer;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    private OffLineMessageService offLineMessageService;

    /**
     * 透传ros发布的topic：agent_pub
     * @param messageInfo
     */
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

    /**
     * 透传ros发布的topic：agent_sub
     * @param messageInfo
     */
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

    /**
     * 透传ros发布的topic：app_pub
     * @param messageInfo
     */
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

    /**
     * 透传ros发布的topic：app_sub
     * @param messageInfo
     */
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

    /**
     * 透传ros发布的topic：current_pose
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_CURRENT_POSE)
    public void directCurrentPose(@Payload MessageInfo messageInfo) {
        try {
            CacheInfoManager.setMessageCache(messageInfo);
        }catch (Exception e){
            logger.error("consumer directCurrentPose exception",e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT)
    public void directCommandReport(@Payload MessageInfo messageInfo) {
        try {
            sendMessageSave(messageInfo);
        }catch (Exception e){
            logger.error("consumer directCommandReport exception",e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE)
    public AjaxResult directCommandReportAndReceive(@Payload MessageInfo messageInfo) {
        try {
            sendMessageSave(messageInfo);
        }catch (Exception e){
            logger.error("consumer directCommandReport exception",e);
        }
        return AjaxResult.success();
    }



    private boolean sendMessageSave(MessageInfo messageInfo) throws Exception{
        if(messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")){
            return false;
        }
        if(MessageType.EXECUTOR_LOG.equals(messageInfo.getMessageType())){
            //TODO 此处可以添加日志及状态上报存储


        }else if(MessageType.REPLY.equals(messageInfo.getMessageType())){
            OffLineMessage message = new OffLineMessage();
            message.setMessageStatusType(messageInfo.getMessageStatusType().getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setUuId(messageInfo.getUuId());//更新的主键
            message.setUpdateTime(new Date());//更新时间
            offLineMessageService.update(message);//更新发送的消息
        }
        return true;
    }

}
