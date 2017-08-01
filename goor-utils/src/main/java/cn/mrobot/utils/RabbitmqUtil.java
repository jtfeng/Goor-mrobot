package cn.mrobot.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class RabbitmqUtil {
    private static Logger logger = LoggerFactory.getLogger(RabbitmqUtil.class);

    /**
     * 获取RabbitMQ的Queue参数
     * @return
     */
    public static Map<String, Object> getRabbitMQArguments() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-max-length", 1000);//最大消息条数
            return args;
        } catch (Exception e) {
            logger.error("getRabbitMQArguments Exception", e);
        }
        return null;
    }

}
