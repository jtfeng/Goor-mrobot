package cn.muye.base.listener;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 * @author Jelynn
 */
@Component
public class AppSubPowerListenerImpl implements TopicCallback{

    private static final Logger logger = LoggerFactory.getLogger(AppSubPowerListenerImpl.class);

    @Override
    public void handleMessage(Message message) {
        try {
            if ((System.currentTimeMillis() - CacheInfoManager.getPowerSendTime()) > 30 * 1000) {//每30秒发送一次电量消息
                logger.info("From ROS ====== power topic  " + message.toString());
                CacheInfoManager.setPowerSendTime();
                ProducerCommon msg = SingleFactory.getProducerCommon();
                msg.sendPowerMessage(message.toString());
            }
        } catch (Exception e) {
            logger.error("AgentSubListenerImpl Exception", e);
        }
    }
}
