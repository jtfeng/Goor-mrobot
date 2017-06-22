package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.cache.CacheInfoManager;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by enva on 2017/6/8.
 */
@Component
public class SingleFactory implements ApplicationContextAware {
    private static volatile Topic heartTopic;
    private static volatile Message message;
    private static volatile TopicCallback topicCallback;
    private static ApplicationContext applicationContext;

    public static Topic getHeartTopicInstance() {
        if (heartTopic == null) {
            synchronized (Topic.class) {
                if (heartTopic == null) {
                    heartTopic = new Topic(applicationContext.getBean(Ros.class), TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
                }
            }
        }
        return heartTopic;
    }

    public static Message getMessageInstance() {
        if (message == null) {
            synchronized (Message.class) {
                if (message == null) {
                    message = new Message(TopicConstants.CHECK_HEART_MESSAGE);
                }
            }
        }
        return message;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SingleFactory.applicationContext = applicationContext;
    }
}
