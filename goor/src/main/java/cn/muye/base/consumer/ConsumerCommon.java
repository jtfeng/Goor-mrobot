package cn.muye.base.consumer;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.MapService;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);
    @Autowired
    private Ros ros;

    //    @Autowired
//    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ReceiveMessageService receiveMessageService;
    @Autowired
    private MapService mapService;

    /**
     * 接收命令消息（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_COMMAND )
    public void topicCommandMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicCommandMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.publishMessage(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("topicCommandMessage Exception", e);
        }
    }

    /**
     * 接收命令消息（有回执）
     * @param messageInfo
     * @return
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RECEIVE_COMMAND )
    public AjaxResult topicCommandAndReceiveMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicCommandAndReceiveMessage=========" + messageInfo);

                //x86 开机定时时间同步
                if (MessageType.TIME_SYNCHRONIZED.equals(messageInfo.getMessageType())) {
                    return clientTimeSynchronized(messageInfo);
                }

                if (MessageType.ROBOT_BATTERY_THRESHOLD.equals(messageInfo.getMessageType())) {
                    String commonInfoStr = messageInfo.getMessageText();
                    CommonInfo commonInfo = JSON.parseObject(commonInfoStr, CommonInfo.class);
                    String robotInfoStr = commonInfo.getPublishMessage();
                    Robot robotInfo = JSON.parseObject(robotInfoStr, Robot.class);
                    //改缓存
                    CacheInfoManager.setRobotInfoCache(robotInfo);
                    //改配置文件
                    updateProperties("application.properties", robotInfo.getBatteryThreshold());
                    return syncRobotBatteryThreshold(messageInfo);
                }

                ScheduledHandleService service = new ScheduledHandleServiceImp();
                return service.publishMessage(ros, messageInfo);
            }

        }catch (Exception e){
            logger.error("topicCommandAndReceiveMessage Exception", e);
        }
        return AjaxResult.failed();
    }

    /**
     * 同步机器人电量阈值
     * @param messageInfo
     * @return
     */
    private AjaxResult syncRobotBatteryThreshold(MessageInfo messageInfo) {
        ScheduledHandleService service = new ScheduledHandleServiceImp();
        return service.publishMessage(ros, messageInfo);
    }

    /**
     * 传递键值对的Map，更新properties文件
     *
     * @param fileName
     *            文件名(放在resource源包目录下)，需要后缀
     * @param batteryThreshold
     *            键值对Map
     */
    public static void updateProperties(String fileName, int batteryThreshold/*Map<String, String> keyValueMap*/) {
        //getResource方法使用了utf-8对路径信息进行了编码，当路径中存在中文和空格时，他会对这些字符进行转换，这样，
        //得到的往往不是我们想要的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的中文及空格路径。
        String filePath = ConsumerCommon.class.getClassLoader().getResource(fileName).getFile();
        Properties props = null;
        BufferedWriter bw = null;

        try {
            filePath = URLDecoder.decode(filePath,"utf-8");
            logger.debug("updateProperties propertiesPath:" + filePath);
            props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(fileName));
            logger.debug("updateProperties old:"+props);

            // 写入属性文件
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

//            props.clear();// 清空旧的文件
            props.setProperty("robot.batteryThreshold", String.valueOf(batteryThreshold));
//            for (String key : keyValueMap.keySet())
//                props.setProperty(key, keyValueMap.get(key));

            logger.debug("updateProperties new:"+props);
            props.store(bw, "");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收群发命令消息（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_COMMAND )
    public void fanoutCommandMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("fanoutCommandMessage=========" + messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.publishMessage(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("fanoutCommandMessage Exception", e);
        }
    }

    /**
     * 接收资源消息（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RESOURCE )
    public void topicResourceMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                this.receiveMessageSave(messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.downloadResource(ros, messageInfo);
                logger.info("topicResourceMessage=========" + messageInfo);
            }
        }catch (Exception e){
            logger.error("topicResourceMessage Exception", e);
        }
    }

    /**
     * 接收资源消息（有回执）
     * @param messageInfo
     * @return
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RECEIVE_RESOURCE )
    public AjaxResult topicResourceAndReceiveMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicResourceAndReceiveMessage=========" + messageInfo);
                this.receiveMessageSave(messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                return service.downloadResource(ros, messageInfo);
            }
        }catch (Exception e){
            logger.error("topicResourceAndReceiveMessage Exception", e);
        }
        return AjaxResult.failed();
    }

    /**
     * 接收群发资源消息（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_RESOURCE )
    public void fanoutResourceMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                this.receiveMessageSave(messageInfo);
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.downloadResource(ros, messageInfo);
                logger.info("fanoutResourceMessage=========" + messageInfo);
            }
        }catch (Exception e){
            logger.error("fanoutResourceMessage Exception", e);
        }
    }

    /**
     * 接收云端发送至x86消息，不往ros发送消息，只处理agent业务（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_CLIENT )
    public void topicClientMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicClientMessage=========" + messageInfo);
                //TODO 业务需求,请调用各自的处理类
            }
        }catch (Exception e){
            logger.error("topicClientMessage Exception", e);
        }
    }

    /**
     * 接收云端发送至x86消息，不往ros发送消息，只处理agent业务（有回执）
     * @param messageInfo
     * @return
     */
    @RabbitListener(queues = TopicConstants.TOPIC_RECEIVE_CLIENT )
    public AjaxResult topicClientAndReceiveMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("topicClientAndReceiveMessage=========" + messageInfo);
                //TODO 业务需求,请调用各自的处理类
            }
        }catch (Exception e){
            logger.error("topicClientAndReceiveMessage Exception", e);
        }
        return AjaxResult.failed();
    }

    /**
     * 接收云端群发至x86消息，不往ros发送消息，只处理agent业务（无回执）
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_CLIENT )
    public void fanoutClientMessage(@Payload MessageInfo messageInfo) {
        try {
            if (messageInfo != null) {
                logger.info("fanoutClientMessage=========" + messageInfo);
                //TODO 业务需求,请调用各自的处理类
            }
        }catch (Exception e){
            logger.error("fanoutClientMessage Exception", e);
        }
    }

    /**
     * 保存需要保存的消息，如资源下载消息
     * @param messageInfo
     * @return
     * @throws Exception
     */
    private boolean receiveMessageSave(MessageInfo messageInfo) throws Exception{
        //保存发送方消息至数据库，处理完业务后以便回执
        if(messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId())){
            return false;
        }
        ReceiveMessage message = new ReceiveMessage(messageInfo);
        message.setSuccess(false);
        message.setSendTime(new Date());
        receiveMessageService.save(message);//保存需要保存的发送消息，如资源下载之类的
        return true;
    }

    /**
     * x86 agent 开机启动后（默认10分钟）请求云端时间同步
     *
     * @param messageInfo
     * @return
     */
    private AjaxResult clientTimeSynchronized(MessageInfo messageInfo) {
        if (StringUtils.isEmpty(messageInfo.getMessageText())) {
            return AjaxResult.success();
        }

        System.out.println("receive server message,currentTime: " + messageInfo.getSendTime());
        //调用ros service进行时间同步
        Date date = new Date();
        long synchronizedTime = date.getTime() / 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(synchronizedTime));

        Service syncTime = new Service(ros, "/sync_system_time", "sync_system_time/UpdateTime");

        String jsonString = "{\"sync_time\": " + synchronizedTime + "}";
        System.out.println("********************" + jsonString);
        ServiceRequest request = new ServiceRequest(jsonString, "sync_system_time/UpdateTime");

        syncTime.callService(request, new ServiceCallback() {
            @Override
            public void handleServiceResponse(ServiceResponse response) {
                System.out.println("the result of calling service " + response.toString());
            }
        });
        return AjaxResult.success();
    }

}
