package cn.muye.base.listener;

import cn.muye.base.cache.CacheInfoManager;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CheckHeartSubListenerImpl implements TopicCallback {

	@Override
	public void handleMessage(Message message) {
		log.info("监听心跳数据======="+message.toString());
		CacheInfoManager.setTopicHeartCheckCache();
	}

}
