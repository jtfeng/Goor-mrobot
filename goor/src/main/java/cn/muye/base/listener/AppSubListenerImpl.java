package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.producer.ProducerCommon;
import cn.muye.service.FixFilePathService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.json.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 14:23
 * Describe:
 * Version:1.0
 */
@Service
public class AppSubListenerImpl implements TopicCallback, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static Logger logger = Logger.getLogger(AppSubListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			if (TopicConstants.DEBUG)
				logger.info("From ROS ====== app_sub topic  " + message.toString());
			if (TopicHandleInfo.checkSubNameIsNeedConsumer(message.toString())) {
				JsonObject jsonObject = message.toJsonObject();
				String data = jsonObject.getString(TopicConstants.DATA);
				JSONObject dataObject = JSON.parseObject(data);
				String subName = dataObject.getString(TopicConstants.SUB_NAME);
				//工控 发送固定路径文件，agent端拦截处理上传文件至后台
				if(TopicConstants.FIXPATH_FILE_QUERY.equals(subName)){
					JSONObject topicData = dataObject.getJSONObject(TopicConstants.DATA);
					String sceneName = topicData.getString("scene_name");
					FixFilePathService fixFilePathService = applicationContext.getBean(FixFilePathService.class);
					fixFilePathService.handleFixFilePath(sceneName);
				}else {
					ProducerCommon msg = SingleFactory.getProducerCommon();
					msg.sendAppSubMessage(message.toString());
				}
			}
		}catch (Exception e){
			logger.error("AppSubListenerImpl error", e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppSubListenerImpl.applicationContext = applicationContext;
	}
}
