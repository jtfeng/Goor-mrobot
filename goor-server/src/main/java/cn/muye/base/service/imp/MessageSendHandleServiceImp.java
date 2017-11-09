package cn.muye.base.service.imp;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class MessageSendHandleServiceImp implements MessageSendHandleService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OffLineMessageService offLineMessageService;

    public MessageSendHandleServiceImp() {

    }

    @Override
    public AjaxResult sendCommandMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter(robotSN, messageInfo, false)) {
            return AjaxResult.failed("parameter error");
        }
        log.info("开始发送sendCommandMessage，toDataBase="+toDataBase+",x86AgentReply="+x86AgentReply+"robotSN="+robotSN);
        return this.sendMessage(toDataBase, x86AgentReply, messageInfo, RabbitMqBean.getRoutingKey(robotSN, x86AgentReply, MessageType.EXECUTOR_COMMAND.name()), TopicConstants.TOPIC_EXCHANGE);
    }

    @Override
    public AjaxResult sendResourceMessage(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter(robotSN, messageInfo, false)) {
            return AjaxResult.failed("parameter error");
        }
        return this.sendMessage(toDataBase, x86AgentReply, messageInfo, RabbitMqBean.getRoutingKey(robotSN, x86AgentReply, MessageType.EXECUTOR_RESOURCE.name()), TopicConstants.TOPIC_EXCHANGE);
    }

    @Override
    public AjaxResult sendToX86Message(boolean toDataBase, boolean x86AgentReply, String robotSN, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter(robotSN, messageInfo, false)) {
            return AjaxResult.failed("parameter error");
        }
        return this.sendMessage(toDataBase, x86AgentReply, messageInfo, RabbitMqBean.getRoutingKey(robotSN, x86AgentReply, MessageType.EXECUTOR_CLIENT.name()), TopicConstants.TOPIC_EXCHANGE);
    }

    @Override
    public AjaxResult sendCommandMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter("", messageInfo, true)) {
            return AjaxResult.failed("parameter error");
        }
        return this.sendMessage(toDataBase, false, messageInfo, "", TopicConstants.FANOUT_COMMAND_EXCHANGE);
    }

    @Override
    public AjaxResult sendResourceMessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter("", messageInfo, true)) {
            return AjaxResult.failed("parameter error");
        }
        return this.sendMessage(toDataBase, false, messageInfo, "", TopicConstants.FANOUT_RESOURCE_EXCHANGE);
    }

    @Override
    public AjaxResult sendToX86MessageAndAll(boolean toDataBase, MessageInfo messageInfo) throws Exception {
        if (!this.verificationParameter("", messageInfo, true)) {
            return AjaxResult.failed("parameter error");
        }
        return this.sendMessage(toDataBase, false, messageInfo, "", TopicConstants.FANOUT_CLIENT_EXCHANGE);
    }


    /**
     * send message
     *
     * @param toDataBase
     * @param messageInfo
     * @param routingKey
     * @param x86Response
     * @return
     * @throws Exception
     */
    private AjaxResult sendMessage(boolean toDataBase, boolean x86Response, MessageInfo messageInfo, String routingKey, String exchange) throws Exception {
        if (toDataBase) {
            messageInfo.setSenderId("cloud");
            messageInfo.setSuccess(false);
            this.saveSendRecord(messageInfo);
        }
        if(StringUtils.isEmpty(routingKey)){
            log.debug("开始批量发送消息");
            rabbitTemplate.convertAndSend(exchange, "", messageInfo);
            return AjaxResult.success("已发送，等待机器人回复");
        }
        if (x86Response) {
            log.info("开始发送带回执消息");
            return (AjaxResult) rabbitTemplate.convertSendAndReceive(exchange, routingKey, messageInfo);
        } else {
            log.info("开始发送无回执消息");
            rabbitTemplate.convertAndSend(exchange, routingKey, messageInfo);
            return AjaxResult.success("已发送，等待机器人回复");
        }
    }

    /**
     * save send message to db
     *
     * @param messageInfo
     * @throws Exception
     */
    private void saveSendRecord(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            throw new Exception();
        }
        OffLineMessage message = new OffLineMessage(messageInfo);
        offLineMessageService.save(message);//保存发送的消息
    }

    /**
     * check parameter
     *
     * @param robotSN
     * @param messageInfo
     * @return
     */
    private boolean verificationParameter(String robotSN, MessageInfo messageInfo, boolean all) throws Exception {
        if (StringUtils.isEmpty(robotSN) && !all) {
            log.error("robotSN为空，取消发送");
            return false;
        }
        if (null == messageInfo
                || StringUtils.isEmpty(messageInfo.getUuId())
                || StringUtils.isEmpty(messageInfo.getReceiverId())
                || null == messageInfo.getMessageType()
                || StringUtils.isEmpty(messageInfo.getMessageType().name())
                || StringUtils.isEmpty(messageInfo.getMessageText())) {
            log.error("发送的messageInfo参数错误,必须包含uuId，receiverId,messageType,messageText，取消发送");
            return false;
        }
        return true;
    }

}
