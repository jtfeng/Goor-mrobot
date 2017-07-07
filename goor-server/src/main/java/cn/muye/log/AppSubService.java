package cn.muye.log;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.ExecutorLog;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.muye.area.point.service.PointService;
import cn.muye.log.charge.bean.ChargeInfo;
import cn.muye.log.charge.service.ChargeInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/5
 * Time: 17:29
 * Describe: 处理工控app_sub topic返回的信息
 * Version:1.0
 */
@Service
public class AppSubService implements ApplicationContextAware {

	private static final String POINT_PREFIX = "point";
	private static final Logger logger = LoggerFactory.getLogger(AppSubService.class);

	private static ApplicationContext applicationContext;

	private ChargeInfoService chargeInfoService;

	private PointService pointService;

	public void handle(ExecutorLog logInfo, String senderId){
		logger.info("senderId = "+ senderId + " logInfo =" +JSON.toJSONString(logInfo));
		JSONObject logDataObject = JSON.parseObject(logInfo.getData());
		SlamResponseBody slamResponseBody = JSON.parseObject(logDataObject.getString(TopicConstants.DATA), SlamResponseBody.class);
		if (TopicConstants.CHARGING_STATUS_INQUIRY.equals(slamResponseBody.getSubName())) {
			ChargeInfo chargeInfo = JSON.parseObject(JSON.toJSONString(slamResponseBody.getData()), ChargeInfo.class);
			chargeInfo.setDeviceId(senderId);
			chargeInfoService = applicationContext.getBean(ChargeInfoService.class);
			chargeInfoService.save(chargeInfo);
		}else if (TopicConstants.MOTION_PLANNER_MOTION_STATUS.endsWith(slamResponseBody.getSubName())){
			//TODO
		}
	}


//	public void sendChargeTopic(String deviceId){
//		//封装数据
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(Constant.PUB_NAME, Constant.CHARGING_STATUS_INQUIRY);
//		JSONObject messageObject = new JSONObject();
//		messageObject.put(Constant.DATA, JSON.toJSONString(jsonObject));
//
//		CommonInfo commonInfo = new CommonInfo();
//		commonInfo.setTopicName(Constant.APP_PUB);
//		commonInfo.setTopicType(Constant.TOPIC_TYPE_STRING);
//		commonInfo.setPublishMessage(JSON.toJSONString(messageObject));
//
//		String text = JSON.toJSONString(commonInfo);
//		byte[] b = text.getBytes();
//		MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
//		info.setMessageStatusType(MessageStatusType.INIT);
////		info.setReceiptWebSocket(true);
////		info.setWebSocketId("user-9");
//		info.setSendDeviceType(DeviceType.GOOR_SERVER);
//		info.setReceiverDeviceType(DeviceType.GOOR);
//		info.setMessageKind(0);
//		info.setSendTime(new Date());
//		info.setUpdateTime(new Date());
//		info.setSendCount(0);
//
//		messageSendService.sendNoStatusMessage(deviceId, info);
//	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppSubService.applicationContext = applicationContext;
	}



}
