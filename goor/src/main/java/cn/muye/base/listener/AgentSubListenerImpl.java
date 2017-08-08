package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
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
		try {
			if (TopicConstants.DEBUG)
				logger.info("From ROS ====== agent_sub topic  " + message.toString());
			if (TopicHandleInfo.checkSubNameIsNeedConsumer(message.toString())) {
				if (TopicConstants.DEBUG)
					logger.info(" ====== message.toString()===" + message.toString());
				ProducerCommon msg = SingleFactory.getProducerCommon();
				msg.sendAgentSubMessage(message.toString());
			}
			if (TopicHandleInfo.checkLocalSubNameNoNeedConsumer(message.toString())) {
				JsonObject jsonObject = message.toJsonObject();
				String data = jsonObject.getString(TopicConstants.DATA);
				JSONObject dataObject = JSON.parseObject(data);
				String subName = dataObject.getString(TopicConstants.SUB_NAME);
				String uuid = dataObject.getString(TopicConstants.UUID);
				boolean handled = CacheInfoManager.getUUIDHandledCache(uuid);
				if (handled)
					logger.info(" UUID 请求已处理, uuid=" + uuid);
				if (TopicConstants.AGENT_LOCAL_MAP_UPLOAD.equals(subName) && (!handled)) {
					FileUpladService fileUpladService = applicationContext.getBean(FileUpladService.class);
					fileUpladService.sendTopic("0", uuid, "请求接收成功");
					fileUpladService.uploadMapFile(uuid);
					CacheInfoManager.setUUIDHandledCache(uuid);
				}
			}
		}catch (Exception e){
			logger.error("AgentSubListenerImpl Exception", e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AgentSubListenerImpl.applicationContext = applicationContext;
	}
}
