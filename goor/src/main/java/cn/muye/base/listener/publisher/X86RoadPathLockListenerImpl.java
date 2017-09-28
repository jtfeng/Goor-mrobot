package cn.muye.base.listener.publisher;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.mission.task.JsonRoadPathLock;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.producer.ProducerCommon;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

import javax.json.JsonObject;

public class X86RoadPathLockListenerImpl implements TopicCallback{
	private static Logger logger = Logger.getLogger(X86RoadPathLockListenerImpl.class);
	@Override
	public void handleMessage(Message message) {
		try {
			logger.info("From ROS ====== X86RoadPathLock topic  " + message.toString());
			JsonObject jsonObject = message.toJsonObject();
			String data = jsonObject.getString(TopicConstants.DATA);
			JsonRoadPathLock jsonRoadPathLock = JSON.parseObject(data, JsonRoadPathLock.class);
			//如果含有result字段，则不处理
			if(jsonRoadPathLock.getResult() != null){
				logger.info("包含result字段。不处理");
				return ;
			}
			String uuid = jsonRoadPathLock.getUuid();
			boolean handled = CacheInfoManager.getUUIDHandledCache(uuid);
			if (handled){
				logger.info(" UUID 请求已处理, uuid=" + uuid);
				return;
			}
			ProducerCommon msg = SingleFactory.getProducerCommon();
			msg.sendX86RoadPathLockMessage(message.toString());
			CacheInfoManager.setUUIDHandledCache(uuid);
		}catch (Exception e){
			logger.error("X86RoadPathLockListenerImpl error",e);
		}
	}
}
