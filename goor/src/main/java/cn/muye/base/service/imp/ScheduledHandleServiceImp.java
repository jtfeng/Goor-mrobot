package cn.muye.base.service.imp;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.CommonInfo;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.download.download.DownloadHandle;
import cn.muye.base.listener.*;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);

    private static ApplicationContext applicationContext;

    private final Lock lockUpgrade = new ReentrantLock();

    private OffLineMessageService offLineMessageService;

    private ReceiveMessageService receiveMessageService;

    private MessageSendService messageSendService;

    private Ros ros;

    private Client client;

    public ScheduledHandleServiceImp(){

    }

    @Override
    public void sendMessage() {
            try {
//                logger.info("Scheduled send message start");
                offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
                messageSendService = applicationContext.getBean(MessageSendService.class);
                List<OffLineMessage> list = offLineMessageService.listByIsSuccess(false);//限制发送超过200次的，不再发送
                for (OffLineMessage message : list) {
//                    if(message.getSendCount() > 200){//限制发送超过200次的，不再发送,后续改xml在查询时过滤
//                        continue;
//                    }
                    MessageInfo info = new MessageInfo(message);
                    messageSendService.sendMessage(message.getReceiverId(), info);
                }
            } catch (final Exception e) {
                logger.error("Scheduled sendMessage exception", e);
            }
    }

    @Override
    public void receiveMessage() {
            try {
//                logger.info("Scheduled send reply message start");
                messageSendService = applicationContext.getBean(MessageSendService.class);
                receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
                List<ReceiveMessage> list = receiveMessageService.listByIsSuccess(new ReceiveMessage(false));//多次回执，未成功和未publish的消息都回执,限制发送超过200次的，不再发送
                for (ReceiveMessage message : list) {
//                    if(message.getSendCount() > 200){//限制发送超过200次的，不再发送,后续改xml在查询时过滤
//                        continue;
//                    }
                    MessageInfo info = new MessageInfo(message);
                    messageSendService.sendReplyMessage(message.getSenderId(), info);
                    message.setSuccess(true);
                    message.setSendCount(message.getSendCount()+1);
                    receiveMessageService.update(message);
                }
            } catch (final Exception e) {
                logger.error("Scheduled receiveMessage exception", e);
            }
    }

    @Override
    public void rosHealthCheck() {
        logger.info("-->> Scheduled rosHealthCheck start");
            try {
                ros = applicationContext.getBean(Ros.class);
                if(null == ros){
                    logger.error("-->> ros is not connect");
                    return;
                }
                Topic topic = new Topic(ros, TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
                topic.publish(SingleFactory.getMessageInstance());//如果已经订阅了，会自动执行订阅方法
                logger.info("rosHealthCheck heartTime=" + CacheInfoManager.getTopicHeartCheckCache());
                if((System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_HEART_TOPIC_MAX){
                    ros.disconnect();
                    ros.connect();
                    TopicCallback checkHeartCallback = new CheckHeartSubListenerImpl();
                    topic.subscribe(checkHeartCallback);
                    topic.publish(SingleFactory.getMessageInstance());
					TopicSubscribeInfo.reSubScribeTopic(ros);//TODO 业务topic subscribe,添加topic时，此处需要添加，以保证断网后能重新订阅到
//					this.reSubScribeTopic();//TODO 业务topic subscribe,添加topic时，此处需要添加，以保证断网后能重新订阅到
                }
            } catch (Exception e) {
                logger.error("-->> Scheduled rosHealthCheck Exception", e);
            }
    }

    @Override
    public void downloadResource(){
            try {
//                logger.info("-->> Scheduled downloadResource start");
                messageSendService = applicationContext.getBean(MessageSendService.class);
                receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
                ros = applicationContext.getBean(Ros.class);
                client = applicationContext.getBean(Client.class);
                List<ReceiveMessage> list = receiveMessageService.listByMessageStatus(new ReceiveMessage(MessageStatusType.FILE_NOT_DOWNLOADED.getIndex()));//从接收的库查询出需要下载的资源
                for (ReceiveMessage message : list) {
                    MessageInfo messageInfo = new MessageInfo(message);
                    DownloadHandle.downloadCheck(ros, client, messageInfo, messageSendService, receiveMessageService);
                }
//                logger.info("-->> Scheduled downloadResource end");
            } catch (final Exception e) {
                logger.error("Scheduled downloadResource Exception", e);
            }
    }

    @Override
    public void downloadResource(Ros ros, Client client, MessageInfo messageInfo){
//        if (this.lockUpgrade.tryLock()) {
        try {
            logger.info("-->> parameter downloadResource start, currentTimeMillis="+ System.currentTimeMillis());
            messageSendService = applicationContext.getBean(MessageSendService.class);
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            DownloadHandle.downloadCheck(ros, client, messageInfo, messageSendService, receiveMessageService);
            logger.info("-->> parameter downloadResource end, currentTimeMillis=" + System.currentTimeMillis());
        } catch (final Exception e) {
            logger.error("Scheduled downloadResource Exception", e);
        }
//            finally {
//                this.lockUpgrade.unlock();
//            }
//        } else {
//            logger.warn("lock downloadResource, but failed.");
//        }
    }

    @Override
    public void publishMessage(){
        try {
//            logger.info("-->> Scheduled publishMessage start");
            ros = applicationContext.getBean(Ros.class);
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            messageSendService = applicationContext.getBean(MessageSendService.class);
            List<ReceiveMessage> list = receiveMessageService.listByMessageStatus(new ReceiveMessage(MessageStatusType.FILE_DOWNLOAD_COMPLETE.getIndex()));//TODO 从接收的库查询出需要发布的资源
            for (ReceiveMessage message : list) {
                CommonInfo commonInfo = JSON.parseObject(message.getMessageText(), CommonInfo.class);
                MessageInfo messageInfo = new MessageInfo(message);
                if(StringUtil.isEmpty(commonInfo.getTopicName())
                        || StringUtil.isEmpty(commonInfo.getTopicType())
                        || StringUtil.isEmpty(commonInfo.getPublishMessage())){
                    message.setMessageStatusType(MessageStatusType.PARAMETER_ERROR.getIndex());
                    this.updateReceiveMessage(message);
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.PARAMETER_ERROR, null);
                    return;
                }
                if((System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache()) < TopicConstants.CHECK_HEART_TOPIC_MAX){
                    Topic echo = new Topic(ros, commonInfo.getTopicName(), commonInfo.getTopicType());
                    Message toSend = new Message(commonInfo.getPublishMessage());
                    echo.publish(toSend);
					//更新发布状态，已经发送
                    message.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE.getIndex());
					this.updateReceiveMessage(message);
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.PUBLISH_ROS_MESSAGE, null);//当设置了需要发送webSocket回执时，发送回执
                }else{
                    message.setMessageStatusType(MessageStatusType.ROS_OFF_LINE.getIndex());
                    this.updateReceiveMessage(message);
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.ROS_OFF_LINE, null);//当设置了需要发送webSocket回执时，发送回执
                    logger.info("-->> publishMessage fail, ros not connect");
                }
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled publishMessage Exception", e);
        }
//        logger.info("-->> Scheduled publishMessage end");
    }

    @Override
    public void publishMessage(Ros ros, Client client, MessageInfo messageInfo){
            try {
                logger.info("-->> parameter publishMessage start");
                messageSendService = applicationContext.getBean(MessageSendService.class);
                CommonInfo commonInfo = JSON.parseObject(messageInfo.getMessageText(), CommonInfo.class);
                if(StringUtil.isEmpty(commonInfo.getTopicName())
                        || StringUtil.isEmpty(commonInfo.getTopicType())
                        || StringUtil.isEmpty(commonInfo.getPublishMessage())){
                    logger.warn("-->> publishMessage commonInfo is null");
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.PARAMETER_ERROR, null);
                    return;
                }
				long end = System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache();
                if((System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache()) < TopicConstants.CHECK_HEART_TOPIC_MAX){
                    Topic echo = new Topic(ros, commonInfo.getTopicName(), commonInfo.getTopicType());
                    Message toSend = new Message(commonInfo.getPublishMessage());
                    echo.publish(toSend);
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.PUBLISH_ROS_MESSAGE, null);
                }else{
                    messageSendService.sendWebSocketMessage(messageInfo,MessageStatusType.ROS_OFF_LINE, null);
                    logger.info("-->> publishMessage fail, ros not connect");
                }
            } catch (Exception e) {
                logger.error("-->> Scheduled publishMessage Exception", e);
            }
    }

    @Override
    public void executeTwentyThreeAtNightPerDay(){
        logger.info("Scheduled clear message start");
        try {
            ReceiveMessage receiveMessage = new ReceiveMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            receiveMessage.setSendTime(new Date());
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(new Date());
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }

//    //当需要发送消息提醒用户时，发送webSocket提醒用户
//    private void receiptWebSocket(MessageInfo messageInfo){
//        //通知socketId
//        messageSendService = applicationContext.getBean(MessageSendService.class);
//        if((System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache()) < Constant.CHECK_HEART_TOPIC_MAX){
//
//        }else if((System.currentTimeMillis()-CacheInfoManager.getTopicHeartCheckCache()) > Constant.CHECK_HEART_TOPIC_MAX){
//
//        }
//    }

	public void updateReceiveMessage(ReceiveMessage message){
		message.setSuccess(false);
        try {
            receiveMessageService.update(message);
        } catch (Exception e) {
            logger.error("update receiveMessage Exception", e);
        }
    }

    //当网络断开时从新订阅
    private void reSubScribeTopic(){
        ros = applicationContext.getBean(Ros.class);

		//订阅工控的topic。所有工控信息全发布在这个topic中，通过sub_name进行区分
		Topic appSubTopic = new Topic(ros, TopicConstants.APP_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appSubCallback = new AppSubListenerImpl();
		appSubTopic.subscribe(appSubCallback);
		//订阅应用发布、工控接收的topic。所有应用信息全发布在这个topic中，通过pub_name进行区分
		Topic appPubTopic = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appPubCallback = new AppPubListenerImpl();
		appPubTopic.subscribe(appPubCallback);
		//订阅agent发布的topic。所有agent发布信息全发布在这个topic中，通过pub_name进行区分
		Topic agentPubTopic = new Topic(ros, TopicConstants.AGENT_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentPubCallback = new AgentPubListenerImpl();
		agentPubTopic.subscribe(agentPubCallback);
		//订阅agent接收的topic。所有agent接收信息全发布在这个topic中，通过sub_name进行区分
		Topic agentSubTopic = new Topic(ros, TopicConstants.AGENT_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentSubCallback = new AgentSubListenerImpl();
		agentSubTopic.subscribe(agentSubCallback);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }
}
