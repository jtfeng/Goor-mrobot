package cn.muye.base.listener.publisher;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86ElevatorLockListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86ElevatorLockListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86ElevatorLock topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86ElevatorLockMessage(message.toString());
		}catch (Exception e){
			logger.error("X86ElevatorLockListenerImpl error",e);
		}
	}


}
