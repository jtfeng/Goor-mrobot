package cn.muye.base.listener.publisher;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionInstantControlListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionInstantControlListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86MissionInstantControl topic  " + message.toString());
		}catch (Exception e){
			logger.error("X86MissionInstantControlListenerImpl error",e);
		}
	}


}
