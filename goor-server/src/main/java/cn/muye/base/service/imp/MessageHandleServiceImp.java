package cn.muye.base.service.imp;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.log.ExecutorLogType;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.CommonInfo;
import cn.muye.base.bean.MessageInfo;
import cn.muye.log.AppSubService;
import cn.muye.base.service.MessageHandleService;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mpush.api.Client;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MessageHandleServiceImp implements MessageHandleService, ApplicationContextAware {
	private static Logger logger = Logger.getLogger(MessageHandleServiceImp.class);

	private static ApplicationContext applicationContext;

	private ScheduledExecutorService scheduledExecutorService;

	private MessageSendService messageSendService;

	private ReceiveMessageService receiveMessageService;

	private AppSubService appSubService;

	private void getScheduledExecutorService() {
		scheduledExecutorService = applicationContext.getBean(ScheduledExecutorService.class);
	}

	@Override
	public void executorCommandMessage(final Client client, final MessageInfo messageInfo) {
		getScheduledExecutorService();
		scheduledExecutorService.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("executorCommandMessage start");
					//TODO 入库等
					logger.info("executorCommandMessage end");
				} catch (Exception e) {
					logger.error("schedule executorCommandMessage exception", e);
				}
			}
		}, 0, TimeUnit.SECONDS);
	}

	public void executorLogMessage(Client client, MessageInfo messageInfo) {
		logger.info("=========================logInfo = " + JSON.toJSONString(messageInfo));
		String messageText = messageInfo.getMessageText();
		if (StringUtil.isNullOrEmpty(messageText)) {
			return;
		}
		ExecutorLog logInfo = JSON.parseObject(messageText, ExecutorLog.class);
		String senderId = messageInfo.getSenderId();
		String data = null;
		try {
			data = JSON.parseObject(logInfo.getData()).getString(TopicConstants.DATA);
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		ExecutorLogType executorLogType = logInfo.getType();
		if (executorLogType.equals(ExecutorLogType.APP_SUB)) {
			//工控的日志
			appSubService = applicationContext.getBean(AppSubService.class);
			appSubService.handle(logInfo, senderId);

			//TODO 根据app_sub内容写业务逻辑

		} else if (executorLogType.equals(ExecutorLogType.AGENT_SUB)){
			//TODO 根据agent_sub内容写业务逻辑
			messageSendService = getMessageSendService();

			System.out.println("############################" + data);

			JSONObject requestDataObject = JSON.parseObject(data);
			String pubName = requestDataObject.getString(TopicConstants.PUB_NAME);

			if(pubName == null) {
				return;
			}

			//TODO 根据pub_name写具体业务逻辑
			//应用上传机器人主板编号，查询可调度站信息接口
			if(pubName.equals(TopicConstants.STATION_LIST_GET)) {
				String pubData = null;
				try {
					pubData = requestDataObject.getString(TopicConstants.DATA);
				} catch (Exception e) {
					logger.error(e);
					return;
				}

				JSONObject dataObject = null;
				String robotCode = null;
				try {
					dataObject = JSON.parseObject(pubData);
					robotCode = dataObject.getString(TopicConstants.ROBOT_CODE);
				} catch (Exception e) {
					logger.error(e);
					return;
				}


				//TODO 从数据库查询该机器人绑定的可到达站的列表
//				List<Station> stationList = service.queryByRobotCode(robotCode);

				//通过agent_pub的topic发送机器人可到达站列表到机器人
				//TODO 现在是假数据，到时候得应用发送机器主板编号，从数据库查该机器人可见的站点信息，返回给应用
				//TODO X86 agent告知应用机器人调度状态信息接口。——通过agent_pub发
				SlamResponseBody slamResponseBody = new SlamResponseBody();
				slamResponseBody.setData("[{\"name\":\"护士站1\"},{\"name\":\"护士站2\"}]");
				slamResponseBody.setSubName(TopicConstants.STATION_LIST_GET);

				JSONObject messageObject = new JSONObject();
				messageObject.put(TopicConstants.DATA, JSON.toJSONString(slamResponseBody));

				CommonInfo commonInfo = new CommonInfo();
				commonInfo.setTopicName(TopicConstants.AGENT_PUB);
				commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
				commonInfo.setPublishMessage(messageObject.toJSONString());
				String text = JSON.toJSONString(commonInfo);
				byte[] b = text.getBytes();
				//用EXECUTOR_COMMAND就会发topic
				MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
				messageSendService.sendNoStatusMessage(robotCode, info);

			}
			else {
				//TODO
			}

		} else {
			//TODO
		}
	}

	@Override
	public void executorResourceMessage(Client client, MessageInfo messageInfo) {

	}

	private MessageSendService getMessageSendService() {
		return applicationContext.getBean(MessageSendService.class);
	}

	@Override
	public void executorUpgradeMessage(final Client client, final MessageInfo messageInfo) {
		if (messageInfo.isFailResend()) {//已经入库的发送，不走以下流程，只有无状态发送才走
			return;
		}
		getScheduledExecutorService();
		scheduledExecutorService.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("-->> executorUpgradeMessage start");
					//TODO 入库等
					logger.info("-->> executorUpgradeMessage end");
				} catch (Exception e) {
					logger.error("schedule executorUpgradeMessage exception", e);
				}
			}
		}, 0, TimeUnit.SECONDS);
	}

    @Override
    public void replyMessage(Client client, MessageInfo messageInfo) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageHandleServiceImp.applicationContext = applicationContext;
    }
}
