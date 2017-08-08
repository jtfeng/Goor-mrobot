package cn.muye.base.listener.publisher;

import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

public class X86MissionHeartbeatListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86MissionHeartbeatListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86MissionHeartbeat topic  " + message.toString());
			if(TopicHandleInfo.checkX86MissionHeartBeatConsumer(message.toString())){
				CacheInfoManager.setX86MissionTopicHeartCheckCache();
			}
		}catch (Exception e){
			logger.error("X86MissionHeartbeatListenerImpl error",e);
		}
	}


}
