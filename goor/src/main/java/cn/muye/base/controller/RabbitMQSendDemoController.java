package cn.muye.base.controller;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.CommonInfo;
import cn.muye.base.bean.MessageInfo;
import cn.muye.publisher.AppSubService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

@Controller
public class RabbitMQSendDemoController {
    private Logger logger = Logger.getLogger(RabbitMQSendDemoController.class);

    @Autowired
    private Ros ros;

	@Autowired
	private AppSubService logPublishService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${local.robot.SN}")
    private String localRobotSN;

	@RequestMapping(value = "testRabbitMq", method= RequestMethod.POST)
	@ResponseBody
	public AjaxResult testRabbitMq(@RequestParam("aa")String aa) {
		MessageInfo info = new MessageInfo();
		info.setUuId(UUID.randomUUID().toString().replace("-", ""));
		info.setSendTime(new Date());
		info.setSenderId(localRobotSN);
		info.setMessageType(MessageType.EXECUTOR_LOG);
		//往云端推送（无回执）
		rabbitTemplate.convertAndSend(TopicConstants.DIRECT_COMMAND_REPORT, info);
		//往云端推送（有回执）
		AjaxResult ajaxResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE, info);
		return AjaxResult.success();
	}
}
