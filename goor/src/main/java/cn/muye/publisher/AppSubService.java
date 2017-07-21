package cn.muye.publisher;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.charge.service.ChargeInfoService;
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

import java.util.Date;

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
    public void sendTopic(String topicName, String topicType, Object data) {
        getRos();
        if (null == ros) {
            LOGGER.error("-->> ros is not connect");
            return;
        }
        Topic echo = new Topic(ros, topicName, topicType);

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(data));
        Message toSend = new Message(JSON.toJSONString(messageObject));
        echo.publish(toSend);
    }

    public void handleLocalTopic(Message message){
        JSONObject jsonObject = JSON.parseObject(message.toString());
        String data = jsonObject.getString(TopicConstants.DATA);
        JSONObject jsonObjectData = JSON.parseObject(data);
        String subName = jsonObjectData.getString(TopicConstants.SUB_NAME);
        String subNameData = jsonObjectData.getString(TopicConstants.DATA);
        switch (subName){
            case TopicConstants.CHARGING_STATUS_INQUIRY:
                saveChargeInfo(subNameData);
                break;
            default:
                break;
        }
    }

    private void saveChargeInfo(String subNameData){
        ChargeInfo chargeInfo = JSON.parseObject(subNameData, ChargeInfo.class);
        chargeInfo.setCreateTime(new Date());
        chargeInfo.setDeviceId(deviceId);
        ChargeInfoService chargeInfoService = applicationContext.getBean(ChargeInfoService.class);
        chargeInfoService.delete(); //删除h2中的数据。h2数据库数据库中只存最新的一条记录
        chargeInfoService.save(chargeInfo);
    }

    private void getRos() {
        ros = applicationContext.getBean(Ros.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppSubService.applicationContext = applicationContext;
    }
}
