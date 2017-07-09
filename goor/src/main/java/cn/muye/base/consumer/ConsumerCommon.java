package cn.muye.base.consumer;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);
    @Autowired
    private Ros ros;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ReceiveMessageService receiveMessageService;

    /**
     * 仅发送的命令消息
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_COMMAND )
    public void topicCommandMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicCommandMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.publishMessage(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("topicCommandMessage Exception", e);
        }
    }

    /**
     * 带回执的命令消息
     * @param messageInfo
     * @return
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RECEIVE_COMMAND )
    public AjaxResult topicCommandAndReceiveMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicCommandAndReceiveMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                return service.publishMessage(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("topicCommandAndReceiveMessage Exception", e);
        }
        return AjaxResult.failed();
    }

    /**
     * 群发命令消息
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_COMMAND )
    public void fanoutCommandMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("fanoutCommandMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.publishMessage(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("fanoutCommandMessage Exception", e);
        }
    }

    /**
     * 仅发送的资源消息
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RESOURCE )
    public void topicResourceMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.downloadResource(ros, messageInfo);
                logger.info("topicResourceMessage=========" + messageInfo);
            }
        }catch (Exception e){
            logger.error("topicResourceMessage Exception", e);
        }
    }

    /**
     * 带回执的资源消息
     * @param messageInfo
     * @return
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RECEIVE_RESOURCE )
    public AjaxResult topicResourceAndReceiveMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicResourceAndReceiveMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                return service.downloadResource(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("topicResourceAndReceiveMessage Exception", e);
        }
        return AjaxResult.failed();
    }

    /**
     * 群发资源消息
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_RESOURCE )
    public void fanoutResourceMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                this.sendMessageSave(messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.downloadResource(ros, messageInfo);
                logger.info("fanoutResourceMessage=========" + messageInfo);
            }
        }catch (Exception e){
            logger.error("fanoutResourceMessage Exception", e);
        }
    }

    private boolean sendMessageSave(MessageInfo messageInfo) throws Exception{
        //保存发送方消息至数据库，处理完业务后以便回执
        if(messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")){
            return false;
        }
//        if(!MessageType.REPLY.equals(messageInfo.getMessageType())){
            ReceiveMessage message = new ReceiveMessage(messageInfo);
            List<ReceiveMessage> messages = receiveMessageService.listByUUID(message);//查询当前发送者有没有发送此记
            if(messages.size() <= 0){
                receiveMessageService.save(message);//未发送保存
            }else{
                ReceiveMessage messageUpdate = new ReceiveMessage();
                messageUpdate.setSuccess(false);//说明发送方未收到回执，需要重新发送回执
                messageUpdate.setUuId(message.getUuId());//更新的联合主键
                messageUpdate.setSenderId(message.getSenderId());//更新的联合主键
                messageUpdate.setUpdateTime(new Date());//更新时间
                receiveMessageService.update(messageUpdate);//发送过了，又发送，说明没收到回执，需要重新发送回执
            }
//        }else if(MessageType.REPLY.equals(messageInfo.getMessageType())){
//            OffLineMessage message = new OffLineMessage();
//            message.setMessageStatusType(messageInfo.getMessageStatusType().getIndex());//如果是回执，将对方传过来的信息带上
//            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
//            message.setSuccess(true);//接收到回执，发送消息成功
//            message.setUuId(messageInfo.getUuId());//更新的主键
//            message.setUpdateTime(new Date());//更新时间
//            offLineMessageService.update(message);//更新发送的消息
//        }
        return true;
    }

}
