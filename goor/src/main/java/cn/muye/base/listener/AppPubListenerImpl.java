package cn.muye.base.listener;

import cn.mrobot.bean.enums.DeviceType;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.log.ExecutorLogType;
import cn.mrobot.bean.constant.Constant;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.config.AppConfig;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.imp.MessageSendServiceImp;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/12
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AppPubListenerImpl implements TopicCallback, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(AppPubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== app_pub topic  " + message.toString());
		String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
		byte[] b = text.getBytes();
		ExecutorLog logInfo = new ExecutorLog();
		logInfo.setData(message.toString());
		logInfo.setType(ExecutorLogType.APP_PUB);

		MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, JSON.toJSONString(logInfo), b);
		info.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE);
		AppConfig appConfig = CacheInfoManager.getAppConfigCache(1L);
		info.setSenderId(appConfig.getMpushUserId());
		info.setSendDeviceType(DeviceType.GOOR);
		info.setReceiverDeviceType(DeviceType.GOOR_SERVER);
		MessageSendService messageSendService = new MessageSendServiceImp();
		messageSendService.sendReplyMessage(Constant.GOOR_SERVER, info);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppPubListenerImpl.applicationContext = applicationContext;
	}
}
