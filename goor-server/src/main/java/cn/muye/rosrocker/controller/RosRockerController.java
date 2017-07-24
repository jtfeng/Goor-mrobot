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
import cn.muye.rosrocker.bean.RosRockerPubBean;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 客户端发送摇杆动作消息给 ROS 处理
     * @param rosRockerDTO
     * @return
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    public Object sendMessageToRos(@RequestBody RosRockerDTO rosRockerDTO){
        try {
            sendDetailMessage(rosRockerDTO.getRobotId(), TopicConstants.ROS_SUB_YAOGAN_TOPIC,
                    TopicConstants.ROS_YAOGAN_TOPIC_TYPE,
                    JSON.toJSONString(new PubData(JSON.toJSONString(rosRockerDTO))));
            return AjaxResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }
    /**
     * 启动控制
     * @return
     */
    @RequestMapping("/start")
    public Object start(String robotId){
        try {
            sendDetailMessage(robotId, TopicConstants.ROS_SUB_YAOGAN_TOPIC, TopicConstants.ROS_YAOGAN_TOPIC_TYPE,
                    JSON.toJSONString(new PubData(JSON.toJSONString(new PubBean(TopicConstants.ROS_ROCKER_CONTROL_START_PUB_NAME)))));
            return AjaxResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }
    /**
     * 结束控制
     * @return
     */
    @RequestMapping("/end")
    public Object end(String robotId){
        try {
            sendDetailMessage(robotId, TopicConstants.ROS_SUB_YAOGAN_TOPIC, TopicConstants.ROS_YAOGAN_TOPIC_TYPE,
                    JSON.toJSONString(new PubData(JSON.toJSONString(new PubBean(TopicConstants.ROS_ROCKER_CONTROL_END_PUB_NAME)))));
            return AjaxResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }
    /**
     * 发送消息
     * @param topicName
     * @param topicType
     * @param content
     */
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
    //Test
    public static void main(String[] args) {
    }
}