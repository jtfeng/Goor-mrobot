package cn.muye.base.consumer;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.config.RobotInfoConfig;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import cn.muye.service.PickUpPswdVerifyService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.Date;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);
    @Autowired
    private Ros ros;
    @Autowired
    private ReceiveMessageService receiveMessageService;

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
                if(MessageType.RABBITMQ_HEARTBEAT.equals(messageInfo.getMessageType())){
                    logger.info("成功接收goor心跳消息-->>"+messageInfo);
                    return AjaxResult.success();
                }
                //x86 开机定时时间同步
                if (MessageType.TIME_SYNCHRONIZED.equals(messageInfo.getMessageType())) {
                    return clientTimeSynchronized(messageInfo);
                }
                if (MessageType.ROBOT_INFO.equals(messageInfo.getMessageType())) {
                    updateRobotInfoConfigRecord(messageInfo);
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
     * 更新机器人信息记录
     * @param messageInfo
     */
    private void updateRobotInfoConfigRecord(MessageInfo messageInfo) {
        String commonInfoStr = messageInfo.getMessageText();
        CommonInfo commonInfo = JSON.parseObject(commonInfoStr, CommonInfo.class);
        String pubDataStr = commonInfo.getPublishMessage();
        PubData pubData = JSON.parseObject(pubDataStr, PubData.class);
        SlamBody slamBody = JSON.parseObject(pubData.getData(), SlamBody.class);
        JSONObject jsonObject = JSON.parseObject(slamBody.getData().toString());
        Robot robot = convertRobotEntity(jsonObject);
        CacheInfoManager.removeRobotInfoConfigCache();
        CacheInfoManager.setRobotInfoConfigCache(robotToRobotInfoConfig(robot));
        //改数据库中的robotInfoConfig表的记录
//        RobotInfoConfig robotInfoConfig = robotToRobotInfoConfig(robot);
//        robotInfoConfigService.update(robotInfoConfig);
    }

    private Robot convertRobotEntity(JSONObject jsonObject) {
        Robot robot = new Robot();
        String uuid = jsonObject.getString(TopicConstants.ROBOT_KEY_UUID);
        String name = jsonObject.getString(TopicConstants.ROBOT_KEY_NAME);
        String code = jsonObject.getString(TopicConstants.ROBOT_KEY_CODE);
        Integer typeId = jsonObject.getInteger(TopicConstants.ROBOT_KEY_TYPE_ID);
        Integer lowBatteryThreshold = jsonObject.getInteger(TopicConstants.ROBOT_KEY_LOW_BATTERY_THRESHOLD);
        Integer sufficientThreshold = jsonObject.getInteger(TopicConstants.ROBOT_KEY_SUFFICIENT_BATTERY_THRESHOLD);
        Boolean busy = jsonObject.getBoolean(TopicConstants.ROBOT_KEY_BUSY);
        Boolean online = jsonObject.getBoolean(TopicConstants.ROBOT_KEY_ONLINE);
        robot.setUuid(uuid);
        robot.setName(name);
        robot.setCode(code);
        robot.setTypeId(typeId);
        robot.setLowBatteryThreshold(lowBatteryThreshold);
        robot.setSufficientBatteryThreshold(sufficientThreshold);
        robot.setBusy(busy);
        robot.setOnline(online);
        return robot;
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

    @Autowired
    PickUpPswdVerifyService pickUpPswdVerifyService;

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
                if (!StringUtils.isEmpty(messageInfo.getMessageText())) {
                    JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                    String data = jsonObject.getString(TopicConstants.DATA);
                    JSONObject jsonObjectData = JSON.parseObject(data);
                    String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                    //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息

                    if (!StringUtil.isEmpty(messageName)) {
                        switch (messageName) {
                            case TopicConstants.PICK_UP_PSWD_VERIFY:
                            /* 17.7.5 Add By Abel. 取货密码验证。根据机器人编号，密码和货柜编号*/
                                return pickUpPswdVerifyService.handlePickUpPswdVerify(messageInfo);
                            default:
                                break;
                        }
                    }
                }
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

        logger.info("receive server synchronized message : " + messageInfo.getMessageText());
        //调用ros service进行时间同步
        Service syncTime = new Service(ros, "/sync_system_time", "sync_system_time/UpdateTime");

        String jsonString = "{\"sync_time\": " + Long.parseLong(messageInfo.getMessageText()) + "}";
        logger.info("time synchronized jsonString ********************" + jsonString);
        ServiceRequest request = new ServiceRequest(jsonString, "sync_system_time/UpdateTime");

        syncTime.callService(request, new ServiceCallback() {
            @Override
            public void handleServiceResponse(ServiceResponse response) {
                logger.info("the result of calling time synchronize service : " + response.toString());
            }
        });
        return AjaxResult.success();
    }

    /**
     * robot转成robotInfoConfig
     * @param robot
     * @return
     */
    private RobotInfoConfig robotToRobotInfoConfig(Robot robot) {
        RobotInfoConfig robotInfoConfig = new RobotInfoConfig();
        robotInfoConfig.setRobotSn(robot.getCode());
        robotInfoConfig.setRobotName(robot.getName());
        robotInfoConfig.setLowBatteryThreshold(robot.getLowBatteryThreshold());
        robotInfoConfig.setRobotStoreId(robot.getStoreId());
        robotInfoConfig.setRobotTypeId(robot.getTypeId());
        return robotInfoConfig;
    }
}
