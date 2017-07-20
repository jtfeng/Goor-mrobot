package cn.muye.base.consumer;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.Constant;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);
    @Autowired
    private Ros ros;
    @Autowired
    private ReceiveMessageService receiveMessageService;
    @Autowired
    private MapService mapService;

    @Value("${local.applicationProperties.path}")
    private String path; //本地application.properties配置文件路径
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
                    updateCacheAndChangeProperties(messageInfo);
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
     * 后台修改了机器人电量阈值需要将Goor的
     * @param messageInfo
     */
    private void updateCacheAndChangeProperties(MessageInfo messageInfo) {
        String commonInfoStr = messageInfo.getMessageText();
        CommonInfo commonInfo = JSON.parseObject(commonInfoStr, CommonInfo.class);
        String pubDataStr = commonInfo.getPublishMessage();
        PubData pubData = JSON.parseObject(pubDataStr, PubData.class);
        Robot robotInfo = JSON.parseObject(pubData.getData(), Robot.class);
        robotInfo.setId(null);
        //改缓存
        CacheInfoManager.setRobotInfoCache(robotInfo);
        //改配置文件
        updateProperties(robotInfo.getBatteryThreshold());
    }

    /**
     * 更新properties文件
     * @param batteryThreshold 键值对Map
     */
    public void updateProperties(int batteryThreshold) {
        Properties prop = new Properties();
        FileInputStream fis;
        BufferedWriter bw = null;
        String filePath = path;
        try {
            fis = new FileInputStream(filePath);
            prop.load(fis);
            // 写入属性文件
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            prop.setProperty(Constant.ROBOT_BATTERY_THRESHOLD, String.valueOf(batteryThreshold));
            prop.store(bw, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //todo SnakeYaml来修改配置文件
//        Yaml yaml = new Yaml();
//        File f = new File(path);
//        //读入文件
//        String result= null;
//        try {
//            result = (String)yaml.load(new FileInputStream(f));
//        } catch (FileNotFoundException e) {
//            logger.error("{}",e);
//        }
//        String[] properties =  result.split("\\s+");
//        if (properties != null && properties.length > 0) {
//            for (String property : properties) {
//                String[] args = property.split("=");
//
//            }
//        }
//        System.out.println( result);
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
