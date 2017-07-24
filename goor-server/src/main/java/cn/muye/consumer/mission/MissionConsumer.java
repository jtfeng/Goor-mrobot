package cn.muye.consumer.mission;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.service.consumer.topic.X86MissionEventService;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
public class MissionConsumer {
    private static Logger logger = Logger.getLogger(MissionConsumer.class);

    @Autowired
    private OffLineMessageService offLineMessageService;

    /**
     * 透传ros发布的topic：x86_mission_queue_response
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_X86_MISSION_QUEUE_RESPONSE)
    public void directX86MissionQueueResponse(@Payload MessageInfo messageInfo) {
        try {

        } catch (Exception e) {
            logger.error("consumer directX86MissionQueueResponse exception", e);
        }
    }

    /**
     * 透传ros发布的topic：x86_mission_state_response
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_X86_MISSION_STATE_RESPONSE)
    public void directX86MissionStateResponse(@Payload MessageInfo messageInfo) {
        try {

        } catch (Exception e) {
            logger.error("consumer directX86MissionStateResponse exception", e);
        }
    }

    @Autowired
    X86MissionEventService x86MissionEventService;

    /**
     * 透传ros发布的topic：x86_mission_event
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_X86_MISSION_EVENT)
    public void directX86MissionEvent(@Payload MessageInfo messageInfo) {
        try {
            //直接service方法处理上报的数据
            x86MissionEventService.handleX86MissionEvent(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directX86MissionEvent exception", e);
        }
    }

    /**
     * 透传ros发布的topic：x86_mission_receive，收到回执消息更新数据库
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_X86_MISSION_RECEIVE)
    public void directX86MissionReceive(@Payload MessageInfo messageInfo) {
        try {
            if(null == messageInfo){
                return;
            }
            JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject jsonObjectData = JSON.parseObject(data);
            String uuId = jsonObjectData.getString(TopicConstants.UUID);
            String code = jsonObjectData.getString(TopicConstants.CODE);
            if(StringUtils.isEmpty(uuId)){
                return;
            }
            //入库，方便查看
            OffLineMessage message = new OffLineMessage();
            message.setMessageStatusType("0".equals(code) ? MessageStatusType.ROBOT_RECEIVE_SUCCESS.getIndex() : MessageStatusType.ROBOT_RECEIVE_FAIL.getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setUuId(uuId);//更新的主键
            message.setReceiverId(messageInfo.getSenderId());
            message.setUpdateTime(messageInfo.getSendTime());//更新时间
            offLineMessageService.update(message);//更新发送的消息
            //接收消息，设置缓存
            MessageInfo cacheInfo = new MessageInfo();
            cacheInfo.setUuId(uuId);
            cacheInfo.setMessageStatusType("0".equals(code) ? MessageStatusType.ROBOT_RECEIVE_SUCCESS : MessageStatusType.ROBOT_RECEIVE_FAIL);
            cacheInfo.setSuccess(true);
            CacheInfoManager.setUUIDCache(uuId, cacheInfo);
        } catch (Exception e) {
            logger.error("consumer directX86MissionReceive exception", e);
        }
    }


}
