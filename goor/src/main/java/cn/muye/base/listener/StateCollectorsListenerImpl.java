package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
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
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class StateCollectorsListenerImpl implements TopicCallback {
	private static Logger logger = Logger.getLogger(StateCollectorsListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
        if (TopicConstants.DEBUG)
		logger.info("From ROS ====== state_collectors topic  " + message.toString());
		if(TopicSubscribeInfo.checkPubNameIsNeedConsumer(message.toString())){
            if (TopicConstants.DEBUG)
			logger.info(" ====== message.toString()===" + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAgentPubMessage(message.toString());
		}
	}
}
