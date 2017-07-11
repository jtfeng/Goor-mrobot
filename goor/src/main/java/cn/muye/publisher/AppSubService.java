package cn.muye.publisher;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamRequestBody;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor
 * User: Jelynn
 * Date: 2017/5/26
 * Time: 10:38
 * Describe: 模拟发送导航的topic
 * Version:1.0
 */
@Service
public class AppSubService implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	private Ros ros;

	public void publishMessage() {
		getRos();
		publishChargeMessage();
		publishMotionMessage();

	}

	public void publishChargeMessage() {
		getRos();
		Topic echo = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(TopicConstants.PUB_NAME, TopicConstants.CHARGING_STATUS_INQUIRY);
		JSONObject messageObject = new JSONObject();
		messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
		Message toSend = new Message(JSON.toJSONString(messageObject));
		echo.publish(toSend);

	}

	public void publishPointMessage() {
		getRos();
		Topic echo = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);

		//目标点载入
		JSONObject pointDataJsonObject = new JSONObject();
		pointDataJsonObject.put(TopicConstants.SCENE_NAME, "example");
		pointDataJsonObject.put(TopicConstants.MAP_NAME, "F001");

		SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.POINT_LOAD);
		slamRequestBody.setData(pointDataJsonObject);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
		Message toSend = new Message(JSON.toJSONString(jsonObject));

		//导航点添加
//		MapPoint mapPoint = new MapPoint();
//		mapPoint.setSceneName("example");
//		mapPoint.setMapName("F001");
//		mapPoint.setPointName("name0");
//		mapPoint.setPointAlias("test");
//		mapPoint.setMapPointTypeId(0);
//		mapPoint.setX(1.0);
//		mapPoint.setY(2.0);
//		mapPoint.setTh(2.0);
//		SlamRequestBody slamRequestBody = new SlamRequestBody("point_add");
//		slamRequestBody.setData(JSON.toJSONString(mapPoint));
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
//		Message toSend = new Message(JSON.toJSONString(jsonObject));

		//目标点删除
//		SlamRequestBody slamRequestBody = new SlamRequestBody("point_delete");
//		JSONObject dataJsonObject = new JSONObject();
//		dataJsonObject.put("scene_name", "asdfs");
//		dataJsonObject.put("map_name","sadf");
//		dataJsonObject.put("point_name","name0");
//		slamRequestBody.setData(dataJsonObject);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
//		Message toSend = new Message(JSON.toJSONString(jsonObject));

		//设置建图方式
//		SlamRequestBody slamRequestBody = new SlamRequestBody("map_module_set");
//		JSONObject dataJsonObject = new JSONObject();
//		dataJsonObject.put("map_use", "gmapping");
//		slamRequestBody.setData(dataJsonObject);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
//		Message toSend = new Message(JSON.toJSONString(jsonObject));

		//获取所有建图方式
//		SlamRequestBody slamRequestBody = new SlamRequestBody("map_modules_get");
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
//		Message toSend = new Message(JSON.toJSONString(jsonObject));

		echo.publish(toSend);
	}

	public void publishMapUploadMessage() {
		getRos();
		Topic echo = new Topic(ros, "agent_pub", TopicConstants.TOPIC_TYPE_STRING);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(TopicConstants.PUB_NAME, "agent_local_map_upload");
		JSONObject messageObject = new JSONObject();
		messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
		Message toSend = new Message(JSON.toJSONString(messageObject));
		echo.publish(toSend);

	}

	public void publishMotionMessage() {
		Topic echo = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(TopicConstants.PUB_NAME, TopicConstants.MOTION_PLANNER_MOTION_STATUS);
		JSONObject messageObject = new JSONObject();
		messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
		Message toSend = new Message(JSON.toJSONString(messageObject));
		echo.publish(toSend);
	}

	private void getRos() {
		ros = applicationContext.getBean(Ros.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AppSubService.applicationContext = applicationContext;
	}
}
