package cn.muye.rosrocker.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.dto.rosrocker.RosRockerDTO;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.service.MessageSendHandleService;
import com.alibaba.fastjson.JSON;
import static com.google.common.base.Preconditions.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wlkfec on 17/07/2017.
 */
@RestController
@RequestMapping("rosRocker")
public class RosRockerController {

    private static final double COORDINATE_CONVERT_RATIO = 0.02;
    private static final double MAX_LINEAR_VELOCITY = 0.7;
    private static final double MAX_ANGULAR_VELOCITY = 0.8;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    public Object sendMessageToRos(@RequestBody Map<String, Object> map){
        try {
            //{"linear":{"y":0.0,"x":-0.288421816682403,"z":0.0},"angular":{"y":0.0,"x":0.0,"z":1.7556886488741245E-7}}
            //robot id ： SNabc0012

            Object robotId = checkNotNull(map.get("robotId"), "机器人编号信息不能为空，请检查!");
            Object X = checkNotNull(map.get("X"), "X 偏移量不能为空，请检查!");
            Object Y = checkNotNull(map.get("Y"), "Y 偏移量不能为空，请检查!");

            // TODO: 02/08/2017 坐标需要经过一个算法转换
            double originalX = Double.parseDouble(String.valueOf(X)), originalY = Double.parseDouble(String.valueOf(Y));
            double changeX = originalY * COORDINATE_CONVERT_RATIO, changeY = originalX * COORDINATE_CONVERT_RATIO * -1;
            TwistObj obj = new TwistObj(){{
                setLinearVelocityX(changeX * MAX_LINEAR_VELOCITY);
                setAngularVelocityZ(changeY * MAX_ANGULAR_VELOCITY);
            }};
            // TODO: 02/08/2017 坐标需要经过一个算法转换

            RosRockerDTO dto = new RosRockerDTO(String.valueOf(robotId),
                    new RosRockerDTO.Linear (obj.getLinearVelocityX()/2,-obj.getLinearVelocityY()/2,0.0),
                    new RosRockerDTO.Angular(0.0,0.0,-obj.getAngularVelocityZ()/2));
            sendDetailMessage(String.valueOf(robotId), TopicConstants.ANDROID_JOYSTICK_CMD_VEL, TopicConstants.ROS_YAOGAN_TOPIC_TYPE, JSON.toJSONString(dto));
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
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, RabbitMqBean.getRoutingKey(robotId, false, MessageType.EXECUTOR_COMMAND.name()), info);
    }

    private static class TwistObj {

        private double linearVelocityX;
        private double linearVelocityY;
        private double angularVelocityZ;

        public double getLinearVelocityX() {
            return linearVelocityX;
        }

        public void setLinearVelocityX(double linearVelocityX) {
            this.linearVelocityX = linearVelocityX;
        }

        public double getLinearVelocityY() {
            return linearVelocityY;
        }

        public void setLinearVelocityY(double linearVelocityY) {
            this.linearVelocityY = linearVelocityY;
        }

        public double getAngularVelocityZ() {
            return angularVelocityZ;
        }

        public void setAngularVelocityZ(double angularVelocityZ) {
            this.angularVelocityZ = angularVelocityZ;
        }
    }


    public static void main(String[] args) {
        double originalX = 10.0, originalY = 10.0;
        double changeX = originalY * COORDINATE_CONVERT_RATIO, changeY = originalX * COORDINATE_CONVERT_RATIO * -1;
        TwistObj obj = new TwistObj(){{
            setLinearVelocityX(changeX * MAX_LINEAR_VELOCITY);
            setAngularVelocityZ(changeY * MAX_ANGULAR_VELOCITY);
        }};
        RosRockerDTO dto = new RosRockerDTO("xxxxx",
                new RosRockerDTO.Linear (obj.getLinearVelocityX(),-obj.getLinearVelocityY(),0.0),
                new RosRockerDTO.Angular(0.0,0.0,-obj.getAngularVelocityZ()));
        System.out.println(dto);
    }

}