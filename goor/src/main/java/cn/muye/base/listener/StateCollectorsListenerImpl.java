package cn.muye.base.listener;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.state.StateCollectorResponse;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.producer.ProducerCommon;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Chay
 * Date: 2017/6/14
 * Time: 14:23
 * Describe: 目前状态机上报为每秒钟20次。所以需要更改向云端上报的频率。添加计数器，没2秒上报一次
 * 根据状态的module 放入缓存，缓存的存活时间为2秒，每次去缓存中检测，存在对应的module。则上传
 * Version:1.0
 */
public class StateCollectorsListenerImpl implements TopicCallback {
    private static Logger logger = Logger.getLogger(StateCollectorsListenerImpl.class);

    @Override
    public void handleMessage(Message message) {
        if (TopicConstants.DEBUG)
            logger.info("From ROS ====== state_collectors topic  " + message.toString());
        if (!StringUtil.isNullOrEmpty(message.toString())) {
            JSONObject jsonObject = JSON.parseObject(message.toString());
            String data = jsonObject.getString(TopicConstants.DATA);
            StateCollectorResponse stateCollectorResponse = JSON.parseObject(data, StateCollectorResponse.class);
            if(!CacheInfoManager.getStateModuleCache(stateCollectorResponse.getModule())){
                //往云端推送
                ProducerCommon msg = SingleFactory.getProducerCommon();
                msg.sendStateCollectorMessage(message.toString());
            }
        }
    }
}
