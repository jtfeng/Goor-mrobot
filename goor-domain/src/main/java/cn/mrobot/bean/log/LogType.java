package cn.mrobot.bean.log;

import cn.mrobot.bean.enums.GoorEnum;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 9:37
 * Describe:
 * Version:1.0
 */
public enum LogType{

	INFO("INFO", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_INFOXX"),  //所有INFO信息  与LogLevel有重复，添加此条主要是为了websocket信息的种类取值统一
	INFO_LOGIN("INFO_LOGIN", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_ZDLXX"),  //站登录信息
	INFO_SCHEDULE_TASK("INFO_SCHEDULE_TASK", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_ZFQDDDXXRZLSDDXX"), //站发起的调度信息日志(历史调度信息)
	INFO_CALL("INFO_CALL", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_FZHJJLRZ"), //分站呼叫记录日志
	INFO_CHARGE("INFO_CHARGE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRCDXX"), //机器人充电信息
	INFO_GOAL_REACHED("INFO_GOAL_REACHED", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DZRZ"), //到站日志
	INFO_EXECUTE_TASK("INFO_EXECUTE_TASK", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_ZXRW"),  //执行任务
	INFO_ELEVATOR("INFO_ELEVATOR", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JCDT"), //进出电梯
	INFO_FLOOR("INFO_FLOOR", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_LC"), //楼层
	INFO_DOOR("INFO_DOOR", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_KGM"),  //开关门
	INFO_ROBOT_RUNNING_TRACK("INFO_ROBOT_RUNNING_TRACK", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_YXGJ"), //运行轨迹
	INFO_VOICE("INFO_VOICE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_YYJH"), //语音交互
	INFO_USER_OPERATE("INFO_USER_OPERATE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_YHCZ"),  //用户操作
	INFO_NAVIGATION("INFO_NAVIGATION", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DH"),  //用户操作
	INFO_BASE("INFO_BASE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DP"),  //用户操作
	INFO_CURRENT_POSE("INFO_CURRENT_POSE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DQWZ"),  //当前位置
	INFO_ORDER("INFO_ORDER", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DD"),  //订单
    INFO_PATH_PLANNING("INFO_PATH_PLANNING", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_LJGH"),  //路径规划
	ELEVATOR_NOTICE("ELEVATOR_NOTICE","goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DTPADXXTZ"),

	WARNING("WARNING", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_WARNINGXX"),  //所有WARNING信息  与LogLevel有重复，添加此条主要是为了websocket信息的种类取值统一
	WARNING_TIMEOUT("WARNING_TIMEOUT", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_CSJG"),  //机器人超时警告
	WARNING_BASE("WARNING_BASE", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DPJG"),  //机器人静止被推动警告
	WARNING_LOWER_POWER("WARNING_LOWER_POWER", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRDDLJG"),  //机器人低电量警告
	WARNING_FORCE_RUN("WARNING_FORCE_RUN", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRJZBTDJG"),  //机器人静止被推动警告
	WARNING_OUT_OF_MAP("WARNING_OUT_OF_MAP", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRTLYZDTQYJG"),  //机器人脱离已知地图区域警告;
	WARNING_ROBOT_FELL("WARNING_ROBOT_FELL", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRSDJG"),  //机器人摔倒警告
	WARNING_PEOPLE_ON_CAR("WARNING_PEOPLE_ON_CAR", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_RDCXCJG"),  //人搭乘小车警告
	WARNING_BUMP("WARNING_BUMP", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_DBJG"),  //颠簸警告
	WARNING_SHOPPING_CART_TIMEOUT("WARNING_SHOPPING_CART_TIMEOUT", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_CSWRCLGWCBJ"),  //超时无人处理购物车报警
	STATION_AVAILABLE_ROBOT_COUNT("STATION_AVAILABLE_ROBOT_COUNT", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_HQZKYJQR"),  //超时无人处理购物车报警
	BOOT_GET_ASSETS("BOOT_GET_ASSETS", "goor_domain_src_main_java_cn_mrobot_bean_log_LogType_java_JQRKJHQYDDXGZY");

	private String name;
	private String value;


	private LogType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static LogType getLogType(String name){
		for(LogType logType : LogType.values()){
			if(logType.getName().endsWith(name)){
				return logType;
			}
		}
		return null;
	}
}
