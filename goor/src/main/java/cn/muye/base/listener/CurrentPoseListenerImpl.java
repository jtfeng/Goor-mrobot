package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class CurrentPoseListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(CurrentPoseListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
            if (TopicConstants.DEBUG)
			logger.info("From ROS ====== CurrentPose topic  " + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendCurrentPoseMessage(message.toString());
		}catch (Exception e){
			logger.error("CurrentPoseListenerImpl error",e);
		}
	}


}
