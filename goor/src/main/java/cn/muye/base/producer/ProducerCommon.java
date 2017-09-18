package cn.muye.base.producer;

import cn.mrobot.bean.constant.TopicConstants;

import cn.muye.base.bean.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.Date;

/**
 * Created by enva on 2017/6/22.
 */
@Component
@Slf4j
public class ProducerCommon implements ApplicationContextAware {
//    private static Logger logger = Logger.getLogger(ProducerCommon.class);
    private static ApplicationContext applicationContext;
    private RabbitTemplate rabbitTemplate;
    private String localRobotSN;

    /**
     * 发布当前位置
     * @param text
     */
    public void sendCurrentPoseMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_CURRENT_POSE, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendCurrentPoseMessage error",e);
        }
    }

    /**
     * 发布当前电量
     * @param text
     */
    public void sendPowerMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_POWER, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendCurrentPoseMessage error",e);
        }
    }

    /**
     * 发布app_sub topic的消息
     * @param text
     */
    public void sendAppSubMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_APP_SUB, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendAppSubMessage error",e);
        }
    }

    /**
     * 发布app_pub topic的消息
     * @param text
     */
    public void sendAppPubMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_APP_PUB, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendAppPubMessage error",e);
        }
    }

    /**
     * 发布agent_sub topic的消息
     * @param text
     */
    public void sendAgentSubMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_AGENT_SUB, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendAgentSubMessage error",e);
        }
    }

    /**
     * 发布state_collector topic的消息 (MQ)
     * @param text
     */
    public void sendAgentPubMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_AGENT_PUB, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendAgentPubMessage error",e);
        }
    }

    /**
     * 发布state_collector topic的消息
     * @param text
     */
    public void sendStateCollectorMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_STATE_COLLECTOR, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendAgentPubMessage error",e);
        }
    }

    /**
     *当前任务队列数据响应topic
     * @param text
     */
    public void sendX86MissionQueueResponseMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_X86_MISSION_QUEUE_RESPONSE, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendX86MissionQueueResponseMessage error",e);
        }
    }

    /**
     *当前任务状态响应topic
     * @param text
     */
    public void sendX86MissionStateResponseMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_X86_MISSION_STATE_RESPONSE, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendX86MissionStateResponseMessage error",e);
        }
    }

    /**
     *任务事件上报topic
     * @param text
     */
    public void sendX86MissionEventMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_X86_MISSION_EVENT, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendX86MissionEventMessage error",e);
        }
    }

    /**
     *任务超时报警上报topic
     * @param text
     */
    public void sendX86MissionAlertMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_X86_MISSION_ALERT, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendX86MissionAlertMessage error",e);
        }
    }

    /**
     *任务回执上报topic
     * @param text
     */
    public void sendX86MissionReceiveMessage(String text){
        try {
            if(!getRabbitTemplate()){
                log.error("getRabbitTemplate is null");
                return;
            }
            if(!getLocalRobotSN()){
                log.error("getLocalRobotSN is null");
                return;
            }
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_X86_MISSION_RECEIVE, new MessageInfo(localRobotSN, new Date(), text));
        }catch (Exception e){
            log.error("sendX86MissionReceiveMessage error",e);
        }
    }

    private boolean getRabbitTemplate(){
        if(null == applicationContext){
            log.error("sendGoorMessage applicationContext is null error");
            return false;
        }
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        if(null == rabbitTemplate){
            log.error("sendGoorMessage rabbitTemplate is null error ");
            return false;
        }
        return true;
    }

    private boolean getLocalRobotSN(){
        if(null == applicationContext){
            log.error("sendGoorMessage applicationContext is null error");
            return false;
        }
        localRobotSN = (String) applicationContext.getBean("localRobotSN");
        if(StringUtils.isEmpty(localRobotSN)){
            log.error("sendGoorMessage localRobotSN is null error ");
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProducerCommon.applicationContext = applicationContext;
    }
}
