package cn.mrobot.bean.constant;

import cn.mrobot.bean.base.PubBean;
import cn.mrobot.bean.base.PubData;
import com.alibaba.fastjson.JSON;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/14
 * Time: 16:29
 * Describe:
 * Version:1.0
 */
public class TopicConstants {

	public static final boolean DEBUG = false;

	public static final String LAGENT_PREFIX = "agent_local"; //需要agent本地处理的topic

	public static final String DATA = "data";

	public static final String TOPIC_TYPE_STRING = "std_msgs/String";//publish的数据类型
	public static final String TOPIC_TYPE_UINT8_ARRAY = "std_msgs/UInt8MultiArray";

	//
	public static final String PUB_NAME = "pub_name";
	public static final String SUB_NAME = "sub_name";

	public static final String DIRECTION_PONG = "pong";
	public static final String DIRECTION = "direction";

	public static final String UUID = "uuid";//与ros通信的uuid
	public static final String CODE = "code";//与ros通信的code

	//工控topic
	public static final String APP_PUB = "/app_pub";
	public static final String APP_SUB = "/app_sub";
	public static final String AGENT_PUB = "/agent_pub";
	public static final String AGENT_SUB = "/agent_sub";
	public static final String CURRENT_POSE = "/current_pose";
	public static final String X86_MISSION_DISPATCH = "/x86_mission_dispatch";//任务下发topic
	public static final String X86_MISSION_QUEUE_CANCEL = "/x86_mission_queue_cancel";//取消任务队列中某些任务列表topic
	public static final String X86_MISSION_INSTANT_CONTROL = "/x86_mission_instant_control";//对当前任务控制topic
	public static final String X86_MISSION_COMMON_REQUEST = "/x86_mission_common_request";//任务数据请求topic
	public static final String X86_MISSION_QUEUE_RESPONSE = "/x86_mission_queue_response";//当前任务队列数据响应topic
	public static final String X86_MISSION_STATE_RESPONSE = "/x86_mission_state_response";//当前任务状态响应topic
	public static final String X86_MISSION_EVENT = "/x86_mission_event";//任务事件上报topic
	public static final String X86_MISSION_RECEIVE = "/x86_mission_receive";//任务回执上报topic
	public static final String X86_ELEVATOR_LOCK = "/x86_elevator_lock";//电梯锁定topic
	public static final String X86_MISSION_HEARTBEAT = "/x86_mission_heartbeat";//x86 mission心跳topic

	public static final String ANDROID_JOYSTICK_CMD_VEL = "/android_joystick_cmd_vel";//摇杆topic
	public static final String ROS_SUB_YAOGAN_TOPIC = "~cmd_vel";
	public static final String ROS_YAOGAN_TOPIC_TYPE = "geometry_msgs/Twist";

	//ros topic pub/sub  name
	public static final String CHARGING_STATUS_INQUIRY = "charging_status_inquiry";
	public static final String PUB_SUB_NAME_ROBOT_INFO = "robot_info"; //下发机器人信息和应用请求机器人信息(电量阈值)的pubName
	public static final String MOTION_PLANNER_MOTION_STATUS = "motion_planner_motion_status";
	public static final String STATE_COLLECTOR = "/state_collector"; //状态机返回状态的topic
	public static final String STATE_REQUEST = "state_request"; //向状态机请求状态的topic
	public static final String ROBOTS_CURRENT_POSE = "robots_current_pose"; //所有机器人当前位置

	public static final String STATION_LIST_GET = "station_list_get";//站信息查询，根据机器人主板编号
	public static final String ROBOT_CODE = "robot_code";//机器人主板编号
	public static final String STATION_INFO = "station_info";//机器人主板编号

	/* 17.7.5 Add By Abel. 取货密码验证。根据机器人编号，密码和货柜编号*/
	public static final String PICK_UP_PSWD_VERIFY = "pick_up_pswd_verify";
	/* 17.8.7 Add By Abel. 更新取货密码信息*/
	public static final String PICK_UP_PSWD_UPDATE = "pick_up_pswd_update";

	//应用下发消息需要获取详细的货物类别信息
	public static final String FETCH_DETAIL_GOODSTYPE = "fetch_detail_goodstype";

	public static final String STATUS_DISPATCH = "status_dispatch";//调度任务状态

	//订阅topic
	public static final String ODOM = "/odom";//TODO test
	public static final String TOPIC_NAV_MSGS = "nav_msgs/Odometry";

	//维持心跳topic
	public static final String CHECK_HEART_TOPIC = "/checkHeartTopic";
	public static final String CHECK_HEART_MESSAGE = "{\"data\": \"heart\"}";
	public static final Long CHECK_HEART_TOPIC_MAX = 30000L;//30秒
	public static final Long CHECK_ROS_BRIDGE_MAX = 90000L;//120秒,检查rosbridge启动情况


	//导航返回的 code 标识
	private static int MOTION_MOVING = 1;
	private static int MOTION_RECEIVED_A_CANCEL_REQUEST = 1;
	private static int MOTION_GOAL_REACHED = 3; //导航到达目标点
	private static int MOTION_ABORTED_THIS_NAVIGATION = 4;
	private static int MOTION_SET_GOAL_FAILED = 5;
	private static int MOTION_CANCEL_THIS_NAVIGATION = 6;
	private static int MOTION_ROBOT_STUCK_IN_THE_PLACE = 10;


	//导航点相关
	public static final String POINT_LOAD = "point_load";
	public static final String SCENE_NAME = "scene_name";
	public static final String MAP_NAME = "map_name";
	public static final String POINTS = "points";

	//机器人相关
	public static final String ROBOT_KEY_UUID = "uuid";
	public static final String ROBOT_KEY_NAME = "name";
	public static final String ROBOT_KEY_CODE = "code";
	public static final String ROBOT_KEY_TYPE_ID = "typeId";
	public static final String ROBOT_KEY_LOW_BATTERY_THRESHOLD = "lowBatteryThreshold";
	public static final String ROBOT_KEY_SUFFICIENT_BATTERY_THRESHOLD = "sufficientBatteryThreshold";
//	public static final String ROBOT_KEY_BOX_ACTIVATED = "boxActivated";
	public static final String ROBOT_KEY_IS_BUSY = "isBusy";
	public static final String ROBOT_KEY_IS_ONLINE = "isOnline";
	public static final String ROBOT_KEY_CHARGER_MAP_POINT_LIST = "chargerMapPointList";
	public static final String ROBOT_KEY_PASSWORDS = "passwords";

	//agent定义的topic
	public static final String AGENT_LOCAL_MAP_UPLOAD = "agent_local_map_upload";
	public static final String ZIP_FILE_SUFFIX = ".zip";


	//pub name
	public static final String MAP_CURRENT_GET = "map_current_get";



	//ros摇杆控制 Topic
	public static final String ROS_ROCKER_CONTROL_START_PUB_NAME = "robot_start";
	public static final String ROS_ROCKER_CONTROL_END_PUB_NAME = "robot_end";



	//sub name



	//定义Queue
	//客户端上报队列
	public static final String DIRECT_CURRENT_POSE = "direct.current_pose";
	public static final String DIRECT_APP_PUB = "direct.app_pub";
	public static final String DIRECT_APP_SUB = "direct.app_sub";
	public static final String DIRECT_AGENT_PUB = "direct.agent_pub";
	public static final String DIRECT_AGENT_SUB = "direct.agent_sub";
	public static final String DIRECT_X86_MISSION_QUEUE_RESPONSE = "direct.x86_mission_queue_response";//当前任务队列数据响应topic
	public static final String DIRECT_X86_MISSION_STATE_RESPONSE = "direct.x86_mission_state_response";//当前任务状态响应topic
	public static final String DIRECT_X86_MISSION_EVENT = "direct.x86_mission_event";//任务事件上报topic
	public static final String DIRECT_X86_MISSION_RECEIVE = "direct.x86_mission_receive";//任务回执上报topic
	public static final String DIRECT_X86_ELEVATOR_LOCK = "direct.x86_elevator_lock";//电梯锁操作请求topic

	public static final String DIRECT_STATE_COLLECTOR = "direct.state_collector"; //状态机上报 queue
	public static final String DIRECT_COMMAND_REPORT = "direct.command_report";
	public static final String DIRECT_COMMAND_REPORT_RECEIVE = "direct.command_report_receive";
	public static final String DIRECT_COMMAND_ROBOT_INFO = "direct.command_robot_info";

	//服务端下发队列，命令队列
	public static final String TOPIC_COMMAND = "#{'topic.command.'+'${local.robot.SN}'}";
	public static final String TOPIC_RECEIVE_COMMAND = "#{'topic.command.receive.'+'${local.robot.SN}'}";
	public static final String FANOUT_COMMAND = "#{'fanout.command.'+'${local.robot.SN}'}";

	//服务端下发队列，资源队列
	public static final String TOPIC_RESOURCE = "#{'topic.resource.'+'${local.robot.SN}'}";
	public static final String TOPIC_RECEIVE_RESOURCE = "#{'topic.resource.receive.'+'${local.robot.SN}'}";
	public static final String FANOUT_RESOURCE = "#{'fanout.resource.'+'${local.robot.SN}'}";

	//服务端下发队列，处理x86agent自己事物队列
	public static final String TOPIC_CLIENT = "#{'topic.client.'+'${local.robot.SN}'}";
	public static final String TOPIC_RECEIVE_CLIENT = "#{'topic.client.receive.'+'${local.robot.SN}'}";
	public static final String FANOUT_CLIENT = "#{'fanout.client.'+'${local.robot.SN}'}";

	//服务端绑定的队列
	public static final String FANOUT_SERVER_COMMAND = "fanout.server";
	public static final String TOPIC_SERVER_COMMAND = "topic.server";

	public static final String TOPIC_COMMAND_ROUTING_KEY = "topic.command.";
	public static final String TOPIC_COMMAND_RECEIVE_ROUTING_KEY = "topic.command.receive.";
	public static final String TOPIC_RESOURCE_ROUTING_KEY = "topic.resource.";
	public static final String TOPIC_RESOURCE_RECEIVE_ROUTING_KEY = "topic.resource.receive.";
	public static final String TOPIC_CLIENT_ROUTING_KEY = "topic.client.";
	public static final String TOPIC_CLIENT_RECEIVE_ROUTING_KEY = "topic.client.receive.";

	//定义server routingKey
	public static final String TOPIC_SERVER_ROUTING_KEY = "topic.#";

	//定义Exchange
	public static final String FANOUT_COMMAND_EXCHANGE = "fanoutCommandExchange";
	public static final String FANOUT_RESOURCE_EXCHANGE = "fanoutResourceExchange";
	public static final String FANOUT_CLIENT_EXCHANGE = "fanoutClientExchange";
	public static final String TOPIC_EXCHANGE = "topicExchange1";

	//pub to ros message
	public static final String GET_CURRENT_MAP_PUB_MESSAGE = JSON.toJSONString(new PubData(JSON.toJSONString(new PubBean(TopicConstants.MAP_CURRENT_GET))));

}
