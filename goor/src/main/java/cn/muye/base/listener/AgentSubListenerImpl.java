package cn.muye.base.listener;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AgentSubListenerImpl implements TopicCallback {

	private static ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(AgentSubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== agent_sub topic  " + message.toString());
		if(TopicSubscribeInfo.checkSubNameIsNeedConsumer(message.toString())){
			logger.info(" ====== message.toString()===" + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAgentSubMessage(message.toString());
		}
	}

}
