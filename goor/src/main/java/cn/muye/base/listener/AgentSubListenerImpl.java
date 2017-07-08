package cn.muye.base.listener;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.DeviceType;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.log.ExecutorLogType;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.config.AppConfig;
import cn.muye.base.service.FileUpladService;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.imp.MessageSendServiceImp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.json.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
public class AgentSubListenerImpl implements TopicCallback, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(AgentSubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== agent_sub topic  " + message.toString());
		JsonObject jsonObject = message.toJsonObject();
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject dataObject = JSON.parseObject(data);
		String pubName = dataObject.getString(TopicConstants.SUB_NAME);
		if (pubName.indexOf(TopicConstants.LAGENT_PREFIX) >= 0) {//需要agent本地处理的topic
			handleAgentLocal(dataObject);
		} else {
			String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
			byte[] b = text.getBytes();
			ExecutorLog logInfo = new ExecutorLog();
			logInfo.setData(message.toString());
			logInfo.setType(ExecutorLogType.AGENT_SUB);

			MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, JSON.toJSONString(logInfo), b);
			info.setMessageStatusType(MessageStatusType.PUBLISH_ROS_MESSAGE);
			AppConfig appConfig = CacheInfoManager.getAppConfigCache(1L);
			info.setSenderId(appConfig.getMpushUserId());
			info.setSendDeviceType(DeviceType.GOOR);
			info.setReceiverDeviceType(DeviceType.GOOR_SERVER);
			MessageSendService messageSendService = new MessageSendServiceImp();
			messageSendService.sendReplyMessage(Constant.GOOR_SERVER, info);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AgentSubListenerImpl.applicationContext = applicationContext;
	}

	private void handleAgentLocal(JSONObject dataObject) {
		String pubName = dataObject.getString(TopicConstants.PUB_NAME);
		if (TopicConstants.AGENT_LOCAL_MAP_UPLOAD.equals(pubName)) {
			FileUpladService fileUpladService = applicationContext.getBean(FileUpladService.class);
			fileUpladService.uploadMapFile();
		}

	}


}
