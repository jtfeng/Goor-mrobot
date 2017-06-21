//package cn.muye.bean;
//
//import java.io.File;
//
///**
// * Created by Administrator on 2016/12/7.
// */
//public class Constant {
//
//	public static final String DATA = "data";
//
//    public static final String UTF_8 = "UTF-8";
//
//    public static final String CONTENT = "content";
//
//    public static final String MESSAGE_TYPE = "messageType";
//
//    public static final String QUESTION_SYMBOL = "?";
//
//    public static final String WITH_SYMBOL = "&";
//
//    public static final String EQUAL_SYMBOL = "=";
//
//    public static final String USER_ID = "userId";
//
//    public static final String TEMP = ".temp";
//
//    public static final String FLAGS = ".flags";
//
//	public static final String FILE_IS_EXIST = "fileIsExist";//文件是否存在
//
//    public static final String TOPIC_TYPE_STRING = "std_msgs/String";//publish的数据类型
//	public static final String TOPIC_TYPE_UINT8_ARRAY = "std_msgs/UInt8MultiArray";
//    //订阅topic
//    public static final String DEMO = "/demo";
//
//	//
//	public static final String PUB_NAME = "pub_name";
//	public static final String SUB_NAME = "sub_name";
//
//	//工控topic
//	public static final String APP_PUB = "/app_pub";
//	public static final String APP_SUB = "/app_sub";
//
//	//ros topic pub/sub  name
//	public static final String CHARGING_STATUS_INQUIRY = "charging_status_inquiry";
//	public static final String MOTION_PLANNER_MOTION_STATUS = "motion_planner_motion_status";
//
//	public static final String STATION_LIST_GET = "station_list_get";//站信息查询，根据机器人主板编号
//	public static final String ROBOT_CODE = "robot_code";//机器人主板编号
//	public static final String STATION_INFO = "station_info";//机器人主板编号
//
//	public static final String STATUS_DISPATCH = "status_dispatch";//调度任务状态
//
//	//导航返回的 code 标识
//	private static int MOTION_MOVING = 1;
//	private static int MOTION_RECEIVED_A_CANCEL_REQUEST = 1;
//	private static int MOTION_GOAL_REACHED = 3; //导航到达目标点
//	private static int MOTION_ABORTED_THIS_NAVIGATION = 4;
//	private static int MOTION_SET_GOAL_FAILED = 5;
//	private static int MOTION_CANCEL_THIS_NAVIGATION = 6;
//	private static int MOTION_ROBOT_STUCK_IN_THE_PLACE = 10;
//
//
//	//查询的常量字段
//	public static String SEARCH_BEGIN_DATE = "beginDate";
//	public static String SEARCH_END_DATE = "endDate";
//	public static String SEARCH_LOG_LEVEL = "logLevel";
//	public static String SEARCH_LOG_TYPE = "logType";
//	public static String SEARCH_DEVICE_ID = "deviceId";
//	public static String SEARCH_NAME = "name";
//
//	public static String SEARCH_PAGE_NO = "pageNo";
//	public static String SEARCH_PAGE_SIZE = "pageSize";
//
//	public static String SEARCH_MISSION_MAIN_ID = "missionMainId";
//	public static String SEARCH_MISSION_CHAIN_ID = "missionChainId";
//	public static String SEARCH_PRIORITY = "priority";
//	public static String SEARCH_FEATURE_ITEM_TYPE_ID = "featureItemTypeId";
//	public static String SEARCH_FEATURE_ITEM_ID = "featureItemId";
//
//}
