package cn.muye.base.listener;

import cn.muye.base.bean.SingleFactory;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.producer.ProducerCommon;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.glassfish.grizzly.http.util.Base64Utils;
import sun.misc.BASE64Decoder;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
@Component
public class PowerListenerImpl implements TopicCallback, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(PowerListenerImpl.class);

    @Override
    public void handleMessage(Message message) {
        try {
            if((System.currentTimeMillis() - CacheInfoManager.getPowerSendTime()) > 30 * 1000){//每30秒发送一次电量消息
                logger.info("From ROS ====== power topic  " + message.toString());
                CacheInfoManager.setPowerSendTime();
                ProducerCommon msg = SingleFactory.getProducerCommon();
                msg.sendPowerMessage(message.toString());
            }

//            JSONObject jsonObject = JSON.parseObject(message.toString());
//            String raw_data = jsonObject.getString("data");
//            byte[] bytes = Base64Utils.decode(raw_data);
//            int[] ret = new int[bytes.length];
//            for (int i = 0; i < bytes.length; i++) {
//                ret[i] = bytes[i] & 0xFF;
//            }
        } catch (Exception e) {
            logger.error("AgentSubListenerImpl Exception", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PowerListenerImpl.applicationContext = applicationContext;
    }
}
