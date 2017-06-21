package cn.muye.log.charge.controller;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.DeviceType;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.CommonInfo;
import cn.muye.base.bean.MessageInfo;
import cn.muye.log.charge.bean.ChargeInfo;
import cn.muye.log.charge.service.ChargeInfoService;
import cn.muye.base.service.MessageSendService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 16:45
 * Describe:
 * Version:1.0
 */
@Controller
public class ChargeInfoController implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private Logger logger = Logger.getLogger(ChargeInfoController.class);

	private ChargeInfoService chargeInfoService;

	@Autowired
	private MessageSendService messageSendService;

	/**
	 * 实时获取电量信息
	 *
	 * @return
	 */
	@RequestMapping(value = "charge/status", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult ChargeStatus(@RequestParam("deviceId") String deviceId) {

		//封装数据
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(TopicConstants.PUB_NAME, TopicConstants.CHARGING_STATUS_INQUIRY);
		JSONObject messageObject = new JSONObject();
		messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));

		CommonInfo commonInfo = new CommonInfo();
		commonInfo.setTopicName(TopicConstants.APP_PUB);
		commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
		commonInfo.setPublishMessage(JSON.toJSONString(messageObject));

		String text = JSON.toJSONString(commonInfo);
		byte[] b = text.getBytes();
		MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
		info.setMessageStatusType(MessageStatusType.INIT);
//		info.setReceiptWebSocket(true);
//		info.setWebSocketId("user-9");
		info.setSendDeviceType(DeviceType.GOOR_SERVER);
		info.setReceiverDeviceType(DeviceType.GOOR);
		info.setMessageKind(0);
		info.setSendTime(new Date());
		info.setUpdateTime(new Date());
		info.setSendCount(0);

		messageSendService.sendNoStatusMessage(deviceId, info);
		return AjaxResult.success();
	}

	/**
	 * 实时获取电量信息
	 *
	 * @return
	 */
	@RequestMapping(value = "charge/lists", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult lists() {
		//TODO
		ChargeInfoService chargeInfoService = applicationContext.getBean(ChargeInfoService.class);
		List<ChargeInfo> list = chargeInfoService.lists();
		return AjaxResult.success();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ChargeInfoController.applicationContext = applicationContext;
	}
}
