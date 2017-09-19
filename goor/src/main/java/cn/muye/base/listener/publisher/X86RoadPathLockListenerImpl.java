package cn.muye.base.listener.publisher;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86RoadPathLockListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86RoadPathLockListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86RoadPathLock topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86RoadPathLockMessage(message.toString());
		}catch (Exception e){
			logger.error("X86RoadPathLockListenerImpl error",e);
		}
	}


}
