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
 * User: Chay
 * Date: 2017/6/12
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AppPubListenerImpl implements TopicCallback {
	private static Logger logger = Logger.getLogger(AppPubListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== app_pub topic  " + message.toString());
		if(TopicSubscribeInfo.checkPubNameIsNeedConsumer(message.toString())){
			logger.info(" ====== message.toString()===" + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAppPubMessage(message.toString());
		}
	}

}
