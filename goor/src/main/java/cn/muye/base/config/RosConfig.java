package cn.muye.base.config;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.listener.CheckHeartSubListenerImpl;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by enva on 2017/6/28.
 */
@Configuration
//@AutoConfigureAfter(RabbitTemplate.class)
public class RosConfig {

    @Value("${ros.path}")
    private String rosPath;

    @Bean
    public Ros ros() {
        Ros ros = new Ros(rosPath);
        ros.connect();
        Topic checkHeartTopic = new Topic(ros, TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
        TopicCallback checkHeartCallback = new CheckHeartSubListenerImpl();
        checkHeartTopic.subscribe(checkHeartCallback);
        TopicSubscribeInfo.reSubScribeTopic(ros);
        return ros;
    }
}
