package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicHandleInfo;
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
		try {
			if (TopicConstants.DEBUG)
				logger.info("From ROS ====== app_sub topic  " + message.toString());
			if (TopicHandleInfo.checkSubNameIsNeedConsumer(message.toString())) {
				ProducerCommon msg = SingleFactory.getProducerCommon();
				msg.sendAppSubMessage(message.toString());
			}
		}catch (Exception e){
			logger.error("AppSubListenerImpl error", e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppSubListenerImpl.applicationContext = applicationContext;
	}
}
