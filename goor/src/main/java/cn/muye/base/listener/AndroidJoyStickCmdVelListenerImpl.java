package cn.muye.base.listener;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

@Slf4j
public class AndroidJoyStickCmdVelListenerImpl implements TopicCallback{
	@Override
	public void handleMessage(Message message) {
		try {
			log.info("From ROS ====== AndroidJoyStickCmdVel topic  " + message.toString());
		}catch (Exception e){
			log.error("AndroidJoyStickCmdVel error",e);
		}
	}


}
