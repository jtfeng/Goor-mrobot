package cn.muye.base.listener;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.log.ExecutorLogType;
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

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AppSubListenerImpl implements TopicCallback {

	private static final Logger logger = LoggerFactory.getLogger(AppSubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== app_sub topic  " + message.toString());
		String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
		byte[] b = text.getBytes();
		ExecutorLog logInfo = new ExecutorLog();
		logInfo.setData(message.toString());
		logInfo.setType(ExecutorLogType.APP_SUB);

		MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, JSON.toJSONString(logInfo), b);
		info.setMessageStatusType(MessageStatusType.INIT);
		AppConfig appConfig = CacheInfoManager.getAppConfigCache(1L);
		info.setSenderId(appConfig.getMpushUserId());
		MessageSendService messageSendService = new MessageSendServiceImp();
		messageSendService.sendNoStatusMessage(Constant.GOOR_SERVER, info);
	}

}
