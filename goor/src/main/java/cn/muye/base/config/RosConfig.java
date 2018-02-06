package cn.muye.base.config;

import cn.muye.base.bean.RosHandlerImp;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.version.bean.MyRos;
import edu.wpi.rail.jrosbridge.Ros;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by enva on 2017/6/28.
 */
@Configuration
@Slf4j
//@AutoConfigureAfter(RabbitTemplate.class)
public class RosConfig {

    @Value("${ros.path}")
    private String rosPath;

    @Bean
    public Ros ros() {
        Ros ros = new Ros(rosPath);
        //TODO 为防止复写的MyRos有问题，暂时先不使用。MyRos只是增加了service失败消息的处理。
//        MyRos ros = new MyRos(rosPath);
        ros.connect();
        ros.addRosHandler(new RosHandlerImp());
        try {
            TopicHandleInfo.topicSubScribe(ros);
            TopicHandleInfo.topicAdvertise(ros);
        } catch (Exception e) {
            log.error("rosConfig get x86_mission_dispatch error", e);
        }
        return ros;
    }
}
