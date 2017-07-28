package cn.muye.rosrocker.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubBean;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.dto.rosrocker.RosRockerDTO;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.rosrocker.bean.RosRockerPubBean;
import com.alibaba.fastjson.JSON;
import static com.google.common.base.Preconditions.*;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wlkfec on 17/07/2017.
 */
@RestController
@RequestMapping("rosRocker")
public class RosRockerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    public Object sendMessageToRos(@RequestBody RosRockerDTO rosRockerDTO){
        try {
            //{"linear":{"y":0.0,"x":-0.288421816682403,"z":0.0},"angular":{"y":0.0,"x":0.0,"z":1.7556886488741245E-7}}
            //robot id ： SNabc0012
            sendDetailMessage(rosRockerDTO.getRobotId(), TopicConstants.ROS_PUB_YAOGAN_TOPIC, TopicConstants.ROS_YAOGAN_TOPIC_TYPE, JSON.toJSONString(rosRockerDTO));
            Thread.sleep(500);
            return AjaxResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    private void sendDetailMessage(String robotId, String topicName, String topicType, String content){
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(topicName);
        commonInfo.setTopicType(topicType);
        commonInfo.setPublishMessage(content);
        MessageInfo info = new MessageInfo();
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotId);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);
        info.setMessageText(JSON.toJSONString(commonInfo));
        rabbitTemplate.convertAndSend(
                TopicConstants.TOPIC_EXCHANGE,
                RabbitMqBean.getRoutingKey(robotId, false,
                        MessageType.EXECUTOR_COMMAND.name()), info);
    }
}