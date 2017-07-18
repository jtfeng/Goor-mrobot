package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionStateResponseListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionStateResponseListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
            if (TopicConstants.DEBUG)
			logger.info("From ROS ====== X86MissionStateResponse topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86MissionStateResponseMessage(message.toString());
		}catch (Exception e){
			logger.error("X86MissionStateResponseListenerImpl error",e);
		}
	}


}
