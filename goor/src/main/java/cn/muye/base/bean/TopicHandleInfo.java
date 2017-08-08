package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.listener.*;
import cn.muye.base.listener.publisher.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通用文件下载bean
 * Created by enva on 2017/5/9.
 */
@Slf4j
public class TopicHandleInfo implements Serializable {
	//当网络断开时从新订阅
	public static void topicSubScribe(Ros ros) throws Exception{
		//心跳
		Topic checkHeartTopic = TopicHandleInfo.getTopic(ros, TopicConstants.CHECK_HEART_TOPIC);
		checkHeartTopic.unsubscribe();
		checkHeartTopic.subscribe(new CheckHeartSubListenerImpl());
		//订阅工控的topic。所有工控信息全发布在这个topic中，通过sub_name进行区分
		Topic appSubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.APP_SUB);
		appSubTopic.unsubscribe();
		appSubTopic.subscribe(new AppSubListenerImpl());
		//订阅应用发布、工控接收的topic。所有应用信息全发布在这个topic中，通过pub_name进行区分
		Topic appPubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.APP_PUB);
		appPubTopic.unsubscribe();
		appPubTopic.subscribe(new AppPubListenerImpl());
		//订阅agent发布的topic。所有agent发布信息全发布在这个topic中，通过pub_name进行区分
		Topic agentPubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_PUB);
		agentPubTopic.unsubscribe();
		agentPubTopic.subscribe(new AgentPubListenerImpl());
		//订阅agent接收的topic。所有agent接收信息全发布在这个topic中，通过sub_name进行区分
		Topic agentSubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_SUB);
		agentSubTopic.unsubscribe();
		agentSubTopic.subscribe(new AgentSubListenerImpl());
		//接收机器人当前位置
		Topic currentPoseTopic = TopicHandleInfo.getTopic(ros, TopicConstants.CURRENT_POSE);
		currentPoseTopic.unsubscribe();
		currentPoseTopic.subscribe(new CurrentPoseListenerImpl());
		// TODO: 17/07/2017 摇杆发布的 Topic 测试
		Topic rosYaoganTopic = TopicHandleInfo.getTopic(ros, TopicConstants.ANDROID_JOYSTICK_CMD_VEL);
		rosYaoganTopic.unsubscribe();
		rosYaoganTopic.subscribe(new AndroidJoyStickCmdVelListenerImpl());

		//当前任务队列数据响应
		Topic queueResponseTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_QUEUE_RESPONSE);
		queueResponseTopic.unsubscribe();
		queueResponseTopic.subscribe(new X86MissionQueueResponseListenerImpl());
		//当前任务状态响应
		Topic stateResponseTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_STATE_RESPONSE);
		stateResponseTopic.unsubscribe();
		stateResponseTopic.subscribe(new X86MissionStateResponseListenerImpl());
		//任务事件上报
		Topic eventTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_EVENT);
		eventTopic.unsubscribe();
		eventTopic.subscribe(new X86MissionEventListenerImpl());
		//云端下发任务回执
		Topic receiveTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_RECEIVE);
		receiveTopic.unsubscribe();
		receiveTopic.subscribe(new X86MissionReceiveListenerImpl());
		//接收机器人当前状态
		Topic stateCollectorsTopic = TopicHandleInfo.getTopic(ros, TopicConstants.STATE_COLLECTOR);
		stateCollectorsTopic.unsubscribe();
		stateCollectorsTopic.subscribe(new StateCollectorsListenerImpl());
	}

	public static void topicAdvertise(Ros ros){
		Topic checkHeartTopic = TopicHandleInfo.getTopic(ros, TopicConstants.CHECK_HEART_TOPIC);
		checkHeartTopic.unadvertise();
		checkHeartTopic.advertise();

		Topic dispatchTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_DISPATCH);
		dispatchTopic.unadvertise();
		dispatchTopic.advertise();
		dispatchTopic.subscribe(new X86MissionDispatchListenerImpl());

		Topic appPubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.APP_PUB);
		appPubTopic.unadvertise();
		appPubTopic.advertise();

		Topic appSubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.APP_SUB);
		appSubTopic.unadvertise();
		appSubTopic.advertise();

		Topic agentPubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_PUB);
		agentPubTopic.unadvertise();
		agentPubTopic.advertise();

		Topic agentSubTopic = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_SUB);
		agentSubTopic.unadvertise();
		agentSubTopic.advertise();

		Topic x86MissionQueueCancelTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_QUEUE_CANCEL);
		x86MissionQueueCancelTopic.unadvertise();
		x86MissionQueueCancelTopic.advertise();
		x86MissionQueueCancelTopic.subscribe(new X86MissionQueueCancelListenerImpl());

		Topic x86MissionInstantControlTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_INSTANT_CONTROL);
		x86MissionInstantControlTopic.unadvertise();
		x86MissionInstantControlTopic.advertise();
		x86MissionInstantControlTopic.subscribe(new X86MissionInstantControlListenerImpl());

		Topic x86MissionCommonRequestTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_COMMON_REQUEST);
		x86MissionCommonRequestTopic.unadvertise();
		x86MissionCommonRequestTopic.advertise();
		x86MissionCommonRequestTopic.subscribe(new X86MissionCommonRequestListenerImpl());

//		Topic x86MissionQueueResponseTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_QUEUE_RESPONSE);
//		x86MissionQueueResponseTopic.unadvertise();
//		x86MissionQueueResponseTopic.advertise();
//
//		Topic x86MissionStateResponseTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_STATE_RESPONSE);
//		x86MissionStateResponseTopic.unadvertise();
//		x86MissionStateResponseTopic.advertise();

//		Topic x86MissionEventTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_EVENT);
//		x86MissionEventTopic.unadvertise();
//		x86MissionEventTopic.advertise();
//
//		Topic x86MissionReceiveTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_RECEIVE);
//		x86MissionReceiveTopic.unadvertise();
//		x86MissionReceiveTopic.advertise();

		Topic x86ElevatorLockTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_ELEVATOR_LOCK);
		x86ElevatorLockTopic.unadvertise();
		x86ElevatorLockTopic.advertise();
		x86ElevatorLockTopic.subscribe(new X86ElevatorLockListenerImpl());

		Topic androidJoyStickCmdVelTopic = TopicHandleInfo.getTopic(ros, TopicConstants.ANDROID_JOYSTICK_CMD_VEL);
		androidJoyStickCmdVelTopic.unadvertise();
		androidJoyStickCmdVelTopic.advertise();

		Topic x86StateCollectorTopic = TopicHandleInfo.getTopic(ros, TopicConstants.STATE_COLLECTOR);
		x86StateCollectorTopic.unadvertise();
		x86StateCollectorTopic.advertise();

		Topic x86MissionHeartbeatTopic = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_HEARTBEAT);
		x86MissionHeartbeatTopic.unadvertise();
		x86MissionHeartbeatTopic.advertise();
		x86MissionHeartbeatTopic.subscribe(new X86MissionHeartbeatListenerImpl());

	}

	public static boolean checkSubNameIsNeedConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
		if(CacheInfoManager.getNameSubCache(messageName)){
		    if (TopicConstants.DEBUG)
			log.info(" ====== message.toString()===" + message);
			return true;
		}
		return false;
	}

	public static boolean checkLocalSubNameNoNeedConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
		if(CacheInfoManager.getNameLSubCache(messageName)){
			return true;
		}
		return false;
	}

	public static boolean checkPubNameIsNeedConsumer(String message) throws Exception {
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
		if(CacheInfoManager.getNameSubCache(messageName)){
            if (TopicConstants.DEBUG)
			log.info(" ====== message.toString()===" + message);
			return true;
		}
		return false;
	}

	public static boolean checkX86MissionHeartBeatConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.DIRECTION);
		if(TopicConstants.DIRECTION_PONG.equals(messageName)){
			return true;
		}
		return false;
	}

	/**
	 * 获取topic对象
	 * @param ros
	 * @param topicName
	 * @return
	 */
	public static Topic getTopic(Ros ros, String topicName){
		if(topicName.equals(TopicConstants.CHECK_HEART_TOPIC)){
			try {
				return SingleFactory.checkHeartTopic(ros);
			} catch (Exception e) {
				log.error("getTopic CHECK_HEART_TOPIC Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_HEARTBEAT)){
			try {
				return SingleFactory.x86_mission_heartbeat(ros);
			} catch (Exception e) {
				log.error("getTopic CHECK_HEART_TOPIC Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_DISPATCH)){
			try {
				return SingleFactory.x86_mission_dispatch(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_DISPATCH Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.APP_PUB)){
			try {
				return SingleFactory.app_pub(ros);
			} catch (Exception e) {
				log.error("getTopic APP_PUB Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.APP_SUB)){
			try {
				return SingleFactory.app_sub(ros);
			} catch (Exception e) {
				log.error("getTopic APP_SUB Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.AGENT_PUB)){
			try {
				return SingleFactory.agent_pub(ros);
			} catch (Exception e) {
				log.error("getTopic AGENT_PUB Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.AGENT_SUB)){
			try {
				return SingleFactory.agent_sub(ros);
			} catch (Exception e) {
				log.error("getTopic AGENT_SUB Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.CURRENT_POSE)){
			try {
				return SingleFactory.current_pose(ros);
			} catch (Exception e) {
				log.error("getTopic CURRENT_POSE Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_QUEUE_CANCEL)){
			try {
				return SingleFactory.x86_mission_queue_cancel(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_QUEUE_CANCEL Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_INSTANT_CONTROL)){
			try {
				return SingleFactory.x86_mission_instant_control(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_INSTANT_CONTROL Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_COMMON_REQUEST)){
			try {
				return SingleFactory.x86_mission_common_request(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_COMMON_REQUEST Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_QUEUE_RESPONSE)){
			try {
				return SingleFactory.x86_mission_queue_response(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_QUEUE_RESPONSE Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_STATE_RESPONSE)){
			try {
				return SingleFactory.x86_mission_state_response(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_STATE_RESPONSE Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_EVENT)){
			try {
				return SingleFactory.x86_mission_event(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_EVENT Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_MISSION_RECEIVE)){
			try {
				return SingleFactory.x86_mission_receive(ros);
			} catch (Exception e) {
				log.error("getTopic X86_MISSION_RECEIVE Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.X86_ELEVATOR_LOCK)){
			try {
				return SingleFactory.x86_elevator_lock(ros);
			} catch (Exception e) {
				log.error("getTopic X86_ELEVATOR_LOCK Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.ANDROID_JOYSTICK_CMD_VEL)){
			try {
				return SingleFactory.android_joystick_cmd_vel(ros);
			} catch (Exception e) {
				log.error("getTopic ANDROID_JOYSTICK_CMD_VEL Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.STATE_COLLECTOR)){
			try {
				return SingleFactory.state_collector(ros);
			} catch (Exception e) {
				log.error("getTopic STATE_COLLECTOR Object error", e);
			}
		}
		return null;
	}

}
