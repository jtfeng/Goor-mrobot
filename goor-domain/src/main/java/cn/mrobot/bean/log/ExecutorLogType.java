package cn.mrobot.bean.log;

/**
 * Created with IntelliJ IDEA.
 * Project Name : mpusher-client-java
 * User: Jelynn
 * Date: 2017/6/2
 * Time: 14:51
 * Describe:
 * Version:1.0
 */
public enum ExecutorLogType {

	APP_SUB,//工控发布，应用接收日志
	APP_PUB,//应用发布，工控接收日志
	AGENT_PUB,//agent发布
	AGENT_SUB;//agent接收

}
