package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionQueueResponseListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionQueueResponseListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
            if (TopicConstants.DEBUG)
			logger.info("From ROS ====== X86MissionQueueResponse topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86MissionQueueResponseMessage(message.toString());
		}catch (Exception e){
			logger.error("X86MissionQueueResponseListenerImpl error",e);
		}
	}


}
