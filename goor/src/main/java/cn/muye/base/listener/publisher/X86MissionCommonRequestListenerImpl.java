package cn.muye.base.listener.publisher;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionCommonRequestListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionCommonRequestListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86MissionCommonRequest topic  " + message.toString());
		}catch (Exception e){
			logger.error("X86MissionCommonRequestListenerImpl error",e);
		}
	}


}
