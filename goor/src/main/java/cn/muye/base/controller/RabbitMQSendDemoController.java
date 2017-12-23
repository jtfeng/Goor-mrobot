package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SingleFactory;
import cn.muye.base.producer.ProducerCommon;
import cn.muye.publisher.AppSubService;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@CrossOrigin
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

	@RequestMapping(value = "testRabbitMq1", method= RequestMethod.POST)
	@ResponseBody
	public AjaxResult testRabbitMq1(@RequestParam("aa")String aa) {
		ProducerCommon msg = SingleFactory.getProducerCommon();
		msg.sendCurrentPoseMessage("ssssssssssssssssssddddddddddddd");


		return AjaxResult.success();
	}

	@RequestMapping(value = "testAgentSub", method= RequestMethod.GET)
	@ResponseBody
	public AjaxResult testAgentSub() {
		try {
//			{
//				sub_name: check_operate_pwd,
//				data: { "input_pwd" : "xxxxx" },
//				uuid : "xxxxx",
//				msg:"",
//				error_code: ""
//			}
			MessageInfo info = new MessageInfo();
			info.setUuId(UUID.randomUUID().toString().replace("-", ""));
			info.setSendTime(new Date());
			info.setSenderId(localRobotSN);
			info.setMessageType(MessageType.EXECUTOR_COMMAND);
			Map<String,String> map = new HashMap<String,String>(){{
				put("data", JSONObject.toJSONString(new HashMap<String,String>(){{
					put("input_pwd","xxxxx");
					put("sub_name","cloud_assets_query");
					put("uuid",UUID.randomUUID().toString().replace("-", ""));
					put("msg","");
					put("error_code","");
				}}));
			}};
			info.setMessageText(JSONObject.toJSONString(map));
			rabbitTemplate.convertAndSend(TopicConstants.DIRECT_AGENT_SUB, info);
			return AjaxResult.success();
		}catch (Exception e){
			return AjaxResult.failed(e.getMessage());
		}
	}
}
