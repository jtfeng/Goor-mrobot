package cn.muye.base.service.imp;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubBean;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.base.PubX86HeartBeat;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.aes.AES;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.download.download.DownloadHandle;
import cn.muye.base.listener.CheckHeartSubListenerImpl;
import cn.muye.base.model.config.RobotInfoConfig;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);
    private static ApplicationContext applicationContext;

    private ReceiveMessageService receiveMessageService;

    private OffLineMessageService offLineMessageService;

    private RabbitTemplate rabbitTemplate;

    private Ros ros;

    private String localRobotSN;

    public ScheduledHandleServiceImp() {

    }

    @Override
    public void receiveMessage() throws Exception {
        try {
            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            getLocalRobotSN();
            List<ReceiveMessage> list = receiveMessageService.listByIsSuccess(new ReceiveMessage(false));//多次回执，未成功和未publish的消息都回执,限制发送超过200次的，不再发送
            for (ReceiveMessage message : list) {
                MessageInfo info = new MessageInfo(message);
                info.setSenderId(localRobotSN);
                info.setMessageType(MessageType.REPLY);
                AjaxResult ajaxResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE, info);
                if(ajaxResult.isSuccess()){
                    message.setSuccess(true);
                    message.setSendCount(message.getSendCount() + 1);
                    receiveMessageService.update(message);
                }
            }
        } catch (final Exception e) {
            logger.error("Scheduled receiveMessage exception", e);
        }
    }

    @Override
    public void rosHealthCheck() throws Exception {
        logger.info("-->> Scheduled rosHealthCheck start");
        try {
            ros = applicationContext.getBean(Ros.class);
            if (null == ros) {
                logger.error("-->> ros is not connect");
                return;
            }
            Topic topic = TopicHandleInfo.getTopic(ros, TopicConstants.CHECK_HEART_TOPIC);
            topic.publish(new Message(TopicConstants.CHECK_HEART_MESSAGE));//如果已经订阅了，会自动执行订阅方法

            //发布app_pub的获取当前地图消息
            Topic mapCurrentPubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.APP_PUB);
            mapCurrentPubTopic.publish(new Message(TopicConstants.GET_CURRENT_MAP_PUB_MESSAGE));
            logger.info("心跳时间："+CacheInfoManager.getTopicHeartCheckCache());
            logger.info("心跳时间差："+(System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()));
//            logger.info("rosHealthCheck heartTime=" + CacheInfoManager.getTopicHeartCheckCache());
            if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_HEART_TOPIC_MAX) {
                ros.disconnect();
                ros.connect();
//                topic.subscribe(new CheckHeartSubListenerImpl());
                logger.info("心跳重新连接->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                TopicHandleInfo.topicSubScribe(ros);//TODO 业务topic subscribe,添加topic时，此处需要添加，以保证断网后能重新订阅到
                TopicHandleInfo.topicAdvertise(ros);
                topic.publish(new Message(TopicConstants.CHECK_HEART_MESSAGE));
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled rosHealthCheck Exception", e);
        }
    }

    @Override
    public void x86MissionRosHealthCheck() throws Exception {
        logger.info("-->> Scheduled x86MissionRosHealthCheck start");
        try {
            ros = applicationContext.getBean(Ros.class);
            if (null == ros) {
                logger.error("-->> ros is not connect");
                return;
            }
            Topic x86MissionHeartBeatTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_HEARTBEAT);
            x86MissionHeartBeatTopic.publish(new Message(JSON.toJSONString(new PubData(JSON.toJSONString(new PubX86HeartBeat(UUID.randomUUID().toString().replace("-", ""), "ping"))))));

            logger.info("x86Mission心跳时间："+CacheInfoManager.getX86MissionTopicHeartCheckCache());
            logger.info("x86Mission心跳时间差："+(System.currentTimeMillis() - CacheInfoManager.getX86MissionTopicHeartCheckCache()));
            if ((System.currentTimeMillis() - CacheInfoManager.getX86MissionTopicHeartCheckCache()) > TopicConstants.CHECK_HEART_TOPIC_MAX) {
                x86MissionHeartBeatTopic.publish(new Message(JSON.toJSONString(new PubData(JSON.toJSONString(new PubX86HeartBeat(UUID.randomUUID().toString().replace("-", ""), "ping"))))));
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled x86MissionRosHealthCheck Exception", e);
        }
    }

    @Override
    public void mqHealthCheck(String queueName) throws Exception {
        try {
            logger.info("Scheduled mqHealthCheck start queueName="+queueName);
            if(!getRabbitTemplate()){
                return;
            }
            if(!getLocalRobotSN()){
                return;
            }
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setSendTime(new Date());
            messageInfo.setSenderId(localRobotSN);
            messageInfo.setMessageType(MessageType.RABBITMQ_HEARTBEAT);
            logger.info("开始发送goor心跳消息");
            rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, queueName, messageInfo);
        } catch (final Exception e) {
            logger.error("Scheduled mqHealthCheck exception", e);
        }
    }

    @Override
    public void downloadResource() throws Exception {
        try {
//            logger.info("-->> Scheduled downloadResource start");
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            ros = applicationContext.getBean(Ros.class);
            List<ReceiveMessage> list = receiveMessageService.listByMessageStatus(new ReceiveMessage(MessageStatusType.FILE_NOT_DOWNLOADED.getIndex()));//从接收的库查询出需要下载的资源
            for (ReceiveMessage message : list) {
                MessageInfo messageInfo = new MessageInfo(message);
                if (null != messageInfo) {
                    CommonInfo commonInfo = JSON.parseObject(messageInfo.getMessageText(), CommonInfo.class);
                    //忽略掉不需要发topic的资源
                    if (null != commonInfo && StringUtils.isEmpty(commonInfo.getTopicName())) {
                        continue;
                    }
                }
                DownloadHandle.downloadCheck(ros, messageInfo, receiveMessageService);
            }
//            logger.info("-->> Scheduled downloadResource end");
        } catch (final Exception e) {
            logger.error("Scheduled downloadResource Exception", e);
        }
    }

    @Override
    public AjaxResult downloadResource(Ros ros, MessageInfo messageInfo) throws Exception {
        try {
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            return DownloadHandle.downloadCheck(ros, messageInfo, receiveMessageService);
        } catch (final Exception e) {
            logger.error("Scheduled downloadResource Exception", e);
        }
        return AjaxResult.failed();
    }

    @Override
    public void publishMessage() throws Exception {
        try {
//            logger.info("-->> Scheduled publishMessage start");
            ros = applicationContext.getBean(Ros.class);
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            List<ReceiveMessage> list = receiveMessageService.listByMessageStatus(new ReceiveMessage(MessageStatusType.FILE_DOWNLOAD_COMPLETE.getIndex()));//TODO 从接收的库查询出需要发布的资源
            for (ReceiveMessage message : list) {
                CommonInfo commonInfo = JSON.parseObject(message.getMessageText(), CommonInfo.class);
                MessageInfo messageInfo = new MessageInfo(message);
                if (StringUtil.isEmpty(commonInfo.getTopicName())
                        || StringUtil.isEmpty(commonInfo.getTopicType())
                        || StringUtil.isEmpty(commonInfo.getPublishMessage())) {
                    message.setMessageStatusType(MessageStatusType.PARAMETER_ERROR.getIndex());
                    this.updateReceiveMessage(message);
                    return;
                }
                if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) < TopicConstants.CHECK_HEART_TOPIC_MAX) {
                    Topic echo = TopicHandleInfo.getTopic(ros, commonInfo.getTopicName());
                    if(null == echo){
                        echo = new Topic(ros, commonInfo.getTopicName(), commonInfo.getTopicType());
                    }
                    Message toSend = new Message(commonInfo.getPublishMessage());
                    echo.publish(toSend);

                    //更新发布状态，已经发送
                    message.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE.getIndex());
                    this.updateReceiveMessage(message);
                } else {
                    message.setMessageStatusType(MessageStatusType.ROS_OFF_LINE.getIndex());
                    this.updateReceiveMessage(message);
                    logger.info("-->> publishMessage fail, ros not connect");
                }
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled publishMessage Exception", e);
        }
    }

    @Override
    public AjaxResult publishMessage(Ros ros, MessageInfo messageInfo) throws Exception {
        try {
//            logger.info("-->> parameter publishMessage start");
            CommonInfo commonInfo = JSON.parseObject(messageInfo.getMessageText(), CommonInfo.class);
            if (StringUtil.isEmpty(commonInfo)
                    || StringUtil.isEmpty(commonInfo.getTopicName())
                    || StringUtil.isEmpty(commonInfo.getTopicType())
                    || StringUtil.isEmpty(commonInfo.getPublishMessage())) {
                logger.warn("-->> publishMessage commonInfo is null");
                return AjaxResult.failed(MessageStatusType.PARAMETER_ERROR.getName());
            }
            if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) < TopicConstants.CHECK_HEART_TOPIC_MAX) {
                Topic echo = TopicHandleInfo.getTopic(ros, commonInfo.getTopicName());
                if(null == echo){
                    echo = new Topic(ros, commonInfo.getTopicName(), commonInfo.getTopicType());
                }
                Message toSend = new Message(commonInfo.getPublishMessage());
                echo.publish(toSend);
//                logger.info("-->> publishMessage commonInfo to ros success");
                return AjaxResult.success(MessageStatusType.PUBLISH_ROS_MESSAGE.getName());
            } else {
                logger.info("-->> publishMessage fail, ros not connect");
                return AjaxResult.failed(MessageStatusType.ROS_OFF_LINE.getName());
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled publishMessage Exception", e);
            return AjaxResult.failed(MessageStatusType.FAILURE_MESSAGE.getName());
        }
    }

    @Override
    public void executeTwentyThreeAtNightPerDay() throws Exception {
        logger.info("Scheduled executeTwentyThreeAtNightPerDay clear message start");
        try {
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
            ReceiveMessage receiveMessage = new ReceiveMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            receiveMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }

    public void updateReceiveMessage(ReceiveMessage message) {
        message.setSuccess(false);
        try {
            receiveMessageService.update(message);
        } catch (Exception e) {
            logger.error("update receiveMessage Exception", e);
        }
    }

    private boolean getRabbitTemplate(){
        if(null == applicationContext){
            logger.error("getRabbitTemplate applicationContext is null error");
            return false;
        }
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        if(null == rabbitTemplate){
            logger.error("getRabbitTemplate rabbitTemplate is null error ");
            return false;
        }
        return true;
    }

    private boolean getLocalRobotSN() {
        if (null == applicationContext) {
            logger.error("getLocalRobotSN applicationContext is null error");
            return false;
        }
        localRobotSN = (String) applicationContext.getBean("localRobotSN");
        if (StringUtils.isEmpty(localRobotSN)) {
            logger.error("getLocalRobotSN localRobotSN is null error ");
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }

    @Override
    public void timeSynchronized(String localRobotSN) {
        try {
            logger.info("Scheduled time synchronized start");
            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setSendTime(new Date());
            messageInfo.setSenderId(localRobotSN);
            messageInfo.setMessageType(MessageType.TIME_SYNCHRONIZED);
            rabbitTemplate.convertAndSend(TopicConstants.DIRECT_COMMAND_REPORT, messageInfo);
        } catch (final Exception e) {
            logger.error("Scheduled time synchronized exception", e);
        }
        System.out.println("*********** x86 time synchronized request ***************");
    }

    @Override
    public void sendRobotInfo() throws Exception {
        ros = applicationContext.getBean(Ros.class);
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        localRobotSN = (String) applicationContext.getBean("localRobotSN");
        MessageInfo info = new MessageInfo();
        RobotInfoConfig robotInfoConfig = CacheInfoManager.getRobotInfoConfigCache();
        Robot robot;
        if (robotInfoConfig != null) {
            robot = robotInfoConfigToRobot(robotInfoConfig);
        } else {
            robot = new Robot();
            robot.setCode(localRobotSN);
        }
        //先往Goor-Server里发自动注册信息
        String robotJson = AES.encryptToBase64(JSON.toJSONString(robot), Constant.AES_KEY);
        info.setMessageText(robotJson);
        info.setSendTime(new Date());
        info.setSenderId(localRobotSN);
        info.setMessageType(MessageType.ROBOT_AUTO_REGISTER);
        logger.info(localRobotSN + "注册信息，发送成功");
        rabbitTemplate.convertAndSend(TopicConstants.DIRECT_COMMAND_ROBOT_INFO, info);
    }

    /**
     * robotInfoConfig转成robot
     * @param robotInfoConfig
     * @return
     */
    private Robot robotInfoConfigToRobot(RobotInfoConfig robotInfoConfig) {
        Robot robot = new Robot();
        robot.setName(robotInfoConfig.getRobotName());
        robot.setCode(robotInfoConfig.getRobotSn());
        robot.setLowBatteryThreshold(robotInfoConfig.getLowBatteryThreshold());
        robot.setStoreId(robotInfoConfig.getRobotStoreId());
        robot.setTypeId(robotInfoConfig.getRobotTypeId());
        return robot;
    }
}
