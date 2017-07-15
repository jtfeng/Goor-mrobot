package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicSubscribeInfo;
import cn.muye.base.producer.ProducerCommon;
import cn.muye.base.service.FileUpladService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
@Component
public class AgentSubListenerImpl implements TopicCallback,ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(AgentSubListenerImpl.class);

	@Override
	public void handleMessage(Message message) {
		logger.info("From ROS ====== agent_sub topic  " + message.toString());
		if(TopicSubscribeInfo.checkSubNameIsNeedConsumer(message.toString())){
			logger.info(" ====== message.toString()===" + message.toString());
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendAgentSubMessage(message.toString());
		}
		if(TopicSubscribeInfo.checkLocalSubNameNoNeedConsumer(message.toString())){
			JsonObject jsonObject = message.toJsonObject();
			String data = jsonObject.getString(TopicConstants.DATA);
			JSONObject dataObject = JSON.parseObject(data);
			String subName = dataObject.getString(TopicConstants.SUB_NAME);
			if (TopicConstants.AGENT_LOCAL_MAP_UPLOAD.equals(subName)) {
				FileUpladService fileUpladService = applicationContext.getBean(FileUpladService.class);
				fileUpladService.uploadMapFile();
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AgentSubListenerImpl.applicationContext = applicationContext;
	}
}
