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

	INFO("INFO", "INFO信息"),  //所有INFO信息  与LogLevel有重复，添加此条主要是为了websocket信息的种类取值统一
	INFO_LOGIN("INFO_LOGIN", "站登录信息"),  //站登录信息
	INFO_SCHEDULE_TASK("INFO_SCHEDULE_TASK", "站发起的调度信息日志(历史调度信息)"), //站发起的调度信息日志(历史调度信息)
	INFO_CALL("INFO_CALL", "分站呼叫记录日志"), //分站呼叫记录日志
	INFO_CHARGE("INFO_CHARGE", "机器人充电信息"), //机器人充电信息
	INFO_GOAL_REACHED("INFO_GOAL_REACHED", "到站日志"), //到站日志
	INFO_EXECUTE_TASK("INFO_EXECUTE_TASK", "执行任务"),  //执行任务
	INFO_ELEVATOR("INFO_ELEVATOR", "进出电梯"), //进出电梯
	INFO_FLOOR("INFO_FLOOR", "楼层"), //楼层
	INFO_DOOR("INFO_DOOR", "开关门"),  //开关门
	INFO_ROBOT_RUNNING_TRACK("INFO_ROBOT_RUNNING_TRACK", "运行轨迹"), //运行轨迹
	INFO_VOICE("INFO_VOICE", "语音交互"), //语音交互
	INFO_USER_OPERATE("INFO_USER_OPERATE", "用户操作"),  //用户操作
	INFO_NAVIGATION("INFO_NAVIGATION", "导航"),  //用户操作
	INFO_BASE("INFO_BASE", "底盘"),  //用户操作
	INFO_CURRENT_POSE("INFO_CURRENT_POSE", "当前位置"),  //当前位置

	WARNING("WARNING", "WARNING信息"),  //所有WARNING信息  与LogLevel有重复，添加此条主要是为了websocket信息的种类取值统一
	WARNING_TIMEOUT("WARNING_TIMEOUT", "超时警告"),  //机器人超时警告
	WARNING_BASE("WARNING_BASE", "底盘警告"),  //机器人静止被推动警告
	WARNING_LOWER_POWER("WARNING_LOWER_POWER", "机器人低电量警告"),  //机器人低电量警告
	WARNING_FORCE_RUN("WARNING_FORCE_RUN", "机器人静止被推动警告"),  //机器人静止被推动警告
	WARNING_OUT_OF_MAP("WARNING_OUT_OF_MAP", "机器人脱离已知地图区域警告"),  //机器人脱离已知地图区域警告;
	WARNING_ROBOT_FELL("WARNING_ROBOT_FELL", "机器人摔倒警告"),  //机器人摔倒警告
	WARNING_PEOPLE_ON_CAR("WARNING_PEOPLE_ON_CAR", "人搭乘小车警告"),  //人搭乘小车警告
	WARNING_BUMP("WARNING_BUMP", "颠簸警告"),  //颠簸警告
	WARNING_SHOPPING_CART_TIMEOUT("WARNING_SHOPPING_CART_TIMEOUT", "超时无人处理购物车报警"),  //超时无人处理购物车报警
	STATION_AVAILABLE_ROBOT_COUNT("STATION_AVAILABLE_ROBOT_COUNT", "获取站可用机器人"),  //超时无人处理购物车报警
	BOOT_GET_ASSETS("BOOT_GET_ASSETS", "机器人开机获取云端的相关资源");

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
