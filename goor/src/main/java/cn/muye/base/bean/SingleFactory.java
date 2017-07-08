package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.springframework.stereotype.Component;

/**
 * Created by enva on 2017/6/8.
 */
@Component
public class SingleFactory {
    private static volatile ProducerCommon msg;

    public static ProducerCommon getProducerCommon() {
        if (msg == null) {
            synchronized (ProducerCommon.class) {
                if (msg == null) {
                    msg = new ProducerCommon();
                }
            }
        }
        return msg;
    }

}
