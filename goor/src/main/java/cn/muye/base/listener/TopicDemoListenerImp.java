package cn.muye.base.listener;

import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;


/**
 * Created by enva on 2017/5/4.
 */
public class TopicDemoListenerImp implements TopicCallback {
        private static Logger logger = Logger.getLogger(TopicDemoListenerImp.class);

        @Override
        public void handleMessage(Message message) {
                logger.info("From ROS: " + message.toString());
        }
}
