package cn.muye.base.listener.publisher;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86StateCollectorListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86StateCollectorListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86StateCollector topic  " + message.toString());
		}catch (Exception e){
			logger.error("X86StateCollectorListenerImpl error",e);
		}
	}


}
