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

    /**
     * 客户端发送摇杆动作消息给 ROS 处理
     * @param rosRockerDTO
     * @return
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    public Object sendMessageToRos(@RequestBody RosRockerDTO rosRockerDTO){
        try {
            //{"linear":{"y":0.0,"x":-0.288421816682403,"z":0.0},"angular":{"y":0.0,"x":0.0,"z":1.7556886488741245E-7}}
            //robot id ： SNabc0012
            sendDetailMessage(rosRockerDTO.getRobotId(), TopicConstants.ROS_PUB_YAOGAN_TOPIC, TopicConstants.ROS_YAOGAN_TOPIC_TYPE, JSON.toJSONString(rosRockerDTO));
            return AjaxResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 通过指定命令 控制机器人或者任务 执行指定的不同操作（包含：pause、resume、clear）
     * @param robotCode
     * @param command
     * @return
     */
    //        SNabc0010_jelynn
    @RequestMapping(value = "/controlState/{robotCode}/{command}", method = RequestMethod.GET)
    public Object controlState(@PathVariable("robotId") String robotCode, @PathVariable("command") String command){
        try {

            checkArgument(!("".equals(robotCode.trim())), "机器人编号不能为空串!");
            checkArgument(!("".equals(robotCode.trim())), "传入的指令不能为空串!");
            checkArgument(ImmutableList.of("pause", "resume", "clear").indexOf(command) != -1, "您传入的指令不正确，合法的指令包括：（ pause、resume、clear ）");

            String uuid = UUID.randomUUID().toString().replace("-", "");
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName(TopicConstants.X86_MISSION_INSTANT_CONTROL);
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            commonInfo.setPublishMessage(JSON.toJSONString(new HashMap<String, String>(){{
                put("command", command);put("uuid", uuid);put("sendTime", String.valueOf(new Date().getTime()));
            }}));
            MessageInfo info = new MessageInfo();
            info.setUuId(uuid);info.setSendTime(new Date());info.setSenderId("goor-server");info.setReceiverId(robotCode);
            info.setMessageType(MessageType.EXECUTOR_COMMAND);info.setMessageText(JSON.toJSONString(commonInfo));

            AjaxResult result = messageSendHandleService.sendCommandMessage(true,true,"robotCode",info);
            if(!result.isSuccess()){
                return AjaxResult.failed(String.format("%s 指令调用失败，请重试", command));
            }
            for (int i=0; i<500; i++) {
                Thread.sleep(1000);
                MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(info.getUuId());
                if(messageInfo1.isSuccess()){
                    info.setSuccess(true);
                    break;
                }
            }
            if (info.isSuccess()) {
                return AjaxResult.success(String.format("%s 指令调用成功!", command));
            }else{
                return AjaxResult.failed(String.format("%s 指令调用失败，请重试", command));
            }
        }catch (Exception e){
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