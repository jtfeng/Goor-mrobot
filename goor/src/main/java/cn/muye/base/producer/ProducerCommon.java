package cn.muye.base.producer;

import cn.mrobot.bean.constant.TopicConstants;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by enva on 2017/6/22.
 */
@Component
public class ProducerCommon implements ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ProducerCommon.class);
    private static ApplicationContext applicationContext;
    private RabbitTemplate rabbitTemplate;

    /**
     * 发布当前位置
     * @param text
     */
    public void sendCurrentPoseMessage(String text){
        try {
            if(getRabbitTemplate()){
                rabbitTemplate.convertAndSend(TopicConstants.DIRECT_CURRENT_POSE, text);
            }
        }catch (Exception e){
            logger.error("sendCurrentPoseMessage error",e);
        }
    }

    /**
     * 发布app_sub topic的消息
     * @param text
     */
    public void sendAppSubMessage(String text){
        try {
            if(getRabbitTemplate()){
                rabbitTemplate.convertAndSend(TopicConstants.DIRECT_APP_SUB, text);
            }
        }catch (Exception e){
            logger.error("sendAppSubMessage error",e);
        }
    }

    /**
     * 发布app_pub topic的消息
     * @param text
     */
    public void sendAppPubMessage(String text){
        try {
            if(getRabbitTemplate()){
                rabbitTemplate.convertAndSend(TopicConstants.DIRECT_APP_PUB, text);
            }
        }catch (Exception e){
            logger.error("sendAppPubMessage error",e);
        }
    }

    /**
     * 发布agent_sub topic的消息
     * @param text
     */
    public void sendAgentSubMessage(String text){
        try {
            if(getRabbitTemplate()){
                rabbitTemplate.convertAndSend(TopicConstants.DIRECT_AGENT_SUB, text);
            }
        }catch (Exception e){
            logger.error("sendAgentSubMessage error",e);
        }
    }

    /**
     * 发布agent_pub topic的消息
     * @param text
     */
    public void sendAgentPubMessage(String text){
        try {
            if(getRabbitTemplate()){
                rabbitTemplate.convertAndSend(TopicConstants.DIRECT_AGENT_PUB, text);
            }
        }catch (Exception e){
            logger.error("sendAgentPubMessage error",e);
        }
    }

//    public void sendCurrentMapMessage(String text){
//        try {
//            if(null == applicationContext){
//                return;
//            }
//            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
//            if(null == rabbitTemplate){
//                return;
//            }
//            rabbitTemplate.convertAndSend("direct.current_map", text);
//        }catch (Exception e){
//            logger.error("SendMessage sendCurrentPoseMessage error",e);
//        }
//    }

    private boolean getRabbitTemplate(){
        if(null == applicationContext){
            logger.error("sendGoorMessage applicationContext is null error");
            return false;
        }
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        if(null == rabbitTemplate){
            logger.error("sendGoorMessage rabbitTemplate is null error ");
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProducerCommon.applicationContext = applicationContext;
    }
}
