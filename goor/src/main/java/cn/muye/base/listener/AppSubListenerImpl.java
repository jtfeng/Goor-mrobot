package cn.muye.base.listener;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.producer.ProducerCommon;
import cn.muye.publisher.AppSubService;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
@Service
public class AppSubListenerImpl implements TopicCallback, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static Logger logger = Logger.getLogger(AppSubListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== app_sub topic  " + message.toString());
		if(TopicSubscribeInfo.checkSubNameIsNeedConsumer(message.toString())){
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAppSubMessage(message.toString());
		}
		if(TopicSubscribeInfo.checkLocalSubNameNoNeedConsumer(message.toString())){
			AppSubService appSubService = applicationContext.getBean(AppSubService.class);
			appSubService.handleLocalTopic(message);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppSubListenerImpl.applicationContext = applicationContext;
	}
}
