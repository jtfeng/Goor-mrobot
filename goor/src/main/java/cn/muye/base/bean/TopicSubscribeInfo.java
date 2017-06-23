package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.listener.AgentPubListenerImpl;
import cn.muye.base.listener.AgentSubListenerImpl;
import cn.muye.base.listener.AppPubListenerImpl;
import cn.muye.base.listener.AppSubListenerImpl;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;

import java.io.Serializable;

/**
 * 通用文件下载bean
 * Created by enva on 2017/5/9.
 */
public class TopicSubscribeInfo implements Serializable {

	//当网络断开时从新订阅
	public static void reSubScribeTopic(Ros ros){

		//订阅工控的topic。所有工控信息全发布在这个topic中，通过sub_name进行区分
		Topic appSubTopic = new Topic(ros, TopicConstants.APP_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appSubCallback = new AppSubListenerImpl();
		appSubTopic.subscribe(appSubCallback);
		//订阅应用发布、工控接收的topic。所有应用信息全发布在这个topic中，通过pub_name进行区分
		Topic appPubTopic = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback appPubCallback = new AppPubListenerImpl();
		appPubTopic.subscribe(appPubCallback);
		//订阅agent发布的topic。所有agent发布信息全发布在这个topic中，通过pub_name进行区分
		Topic agentPubTopic = new Topic(ros, TopicConstants.AGENT_PUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentPubCallback = new AgentPubListenerImpl();
		agentPubTopic.subscribe(agentPubCallback);
		//订阅agent接收的topic。所有agent接收信息全发布在这个topic中，通过sub_name进行区分
		Topic agentSubTopic = new Topic(ros, TopicConstants.AGENT_SUB, TopicConstants.TOPIC_TYPE_STRING);
		TopicCallback agentSubCallback = new AgentSubListenerImpl();
		agentSubTopic.subscribe(agentSubCallback);
	}
}
