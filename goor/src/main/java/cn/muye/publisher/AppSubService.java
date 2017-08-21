package cn.muye.publisher;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.TopicHandleInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/5/26
 * Time: 10:38
 * Describe: 模拟发送导航的topic
 * Version:1.0
 */
@Service
public class AppSubService implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppSubService.class);
    private static ApplicationContext applicationContext;
    private Ros ros;

    @Value("${local.robot.SN}")
    private String deviceId;

    /**
     * 向机器人发送topic消息，获取信息
     * @param topicName
     * @param topicType
     * @param data
     */
    public void sendTopic(String topicName, String topicType, Object data) throws Exception {
        getRos();
        if (null == ros || StringUtils.isEmpty(topicName) || StringUtils.isEmpty(topicType)) {
            LOGGER.error("-->> ros is not connect");
            return;
        }
        if (StringUtils.isEmpty(topicName) || StringUtils.isEmpty(topicType)) {
            LOGGER.error("-->> topicName and topicType is null");
            return;
        }
        Topic echo = TopicHandleInfo.getTopic(ros, topicName);

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(data));
        Message toSend = new Message(JSON.toJSONString(messageObject));
        echo.publish(toSend);
    }

    private void getRos() {
        ros = applicationContext.getBean(Ros.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppSubService.applicationContext = applicationContext;
    }
}
