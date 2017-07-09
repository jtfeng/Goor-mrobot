package cn.muye.base.listener;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AppSubListenerImpl implements TopicCallback {
	private static Logger logger = Logger.getLogger(AppSubListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== app_sub topic  " + message.toString());
		if(TopicSubscribeInfo.checkSubNameIsNeedConsumer(message.toString())){
			logger.info(" ====== message.toString()===" + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAppSubMessage(message.toString());
		}
	}

}
