package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionEventListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionEventListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
            if (TopicConstants.DEBUG)
			logger.info("From ROS ====== X86MissionEvent topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86MissionEventMessage(message.toString());
		}catch (Exception e){
			logger.error("X86MissionEventListenerImpl error",e);
		}
	}


}
