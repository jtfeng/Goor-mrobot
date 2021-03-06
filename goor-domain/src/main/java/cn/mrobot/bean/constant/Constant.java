package cn.mrobot.bean.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class Constant {

    //数据库数据删除状态
    public final static Integer NORMAL = 0; //正常值
    public final static Integer DELETE = 1;  //删除

    //PathType
    public final static Integer PATH_TYPE_CLOUD = 0; //云端路径
    public final static Integer PATH_TYPE_X86 = 1;  //工控路径

    public static final String UTF_8 = "UTF-8";

    public static final String CONTENT = "content";

    public static final String MESSAGE_TYPE = "messageType";

    public static final String QUESTION_SYMBOL = "?";

    public static final String WITH_SYMBOL = "&";

    public static final String EQUAL_SYMBOL = "=";

    public static final String USER_ID = "userId";

    public static final String ID = "id";

    public static final String TEMP = ".temp";

    public static final String FLAGS = ".flags";

    public static final String FILE_IS_EXIST = "fileIsExist";//文件是否存在

    public static final String GOOR_SERVER = "goor-server";

    //文件上传状态
    public final static Integer FILE_OK = 0; //传输成功
    public final static Integer FILE_FAIL = 1;  //传输失败
    public final static String FILE_UPLOAD_TYPE_MAP = "map"; //文件上传类型 ： map: 地图

    public final static String EXPORT_DIR_NAME = "export"; //导出文件夹路径
    public final static String LOG_DIR_NAME= "log"; //日志文件夹路径

    public static final String AUTHORIZE_USERNAME = "web";

    public static final String AUTHORIZE_PASSWORD = "web_secret";

    public static final int ERROR_CODE_NOT_AUTHORIZED = 40001; //未授权

    public static final int ERROR_CODE_NOT_LOGGED = 40002; //未登录

    public static final long MANUAL_ORDER_ID = -10086; //手动调度的订单号

    //地图文件场景下的点和地图路径名
    public final static String MAP_FILE_PATH = "map"; // 地图
    public final static String POINT_FILE_PATH = "point"; // 导航目标点
    public final static String ROS_MAP_FILE_SUFFIX = ".pgm"; // 导航目标点

    public final static String SCENE_MAP_NAME = "sceneMapName"; // 场景地图名

    public final static String AES_KEY = "trOmVF7Uaqd4h9AN"; //自助注册AES校验码

    public final static String ROBOT_CACHE_KEY = "robotInfo"; //自动注册机器人缓存KEY

    public final static String MISSION_LIST_TYPE_PATROL = "patrol"; //巡逻任务-美亚
    public final static String MISSION_LIST_TYPE_CHARGE = "charge"; //充电任务-美亚
    public final static String MISSION_LIST_TYPE_NORMAL = "normal"; //普通任务——任务管理器

    public final static Long CHECK_IF_OFFLINE_TIME = 60000L; //5分钟=30000毫秒

    public final static Long LEAVE_CHARGER_DELAY_TIME = 60000L; //1分钟=60000毫秒

    //与地图点相关的指令列表
    public final static List<Long> ORDER_MAP_POINT_RELATE_LIST = Arrays.asList(
            1L,//单点导航
            4L,//自动充电
            5L//离开充电桩
    );



    public final static String ROBOT_BATTERY_THRESHOLD = "local.robot.batteryThreshold"; //机器人电量阈值配置的Key

    public final static int ROBOT_LOW_BATTERY_THRESHOLD_DEFAULT = 50; //机器人低电量阈值默认值

    public final static int ROBOT_SUFFICIENT_BATTERY_THRESHOLD_DEFAULT = 80; //机器人足电量阈值默认值

    public final static long ORDER_GOTO_CHARGE_ID = 4L;//自动回充任务ID

    public final static long ORDER_LEAVE_CHARGE_ID = 5L;//离开充电桩任务ID

    public final static long ORDER_WAIT_ID = 6L;//等待任务ID

    public final static long ORDER_TIME_CHARGE_ID = 7L;//带时间长度的充电任务ID

    public final static long ORDER_LASER_NAVIGATION = 1L;//单点导航任务ID

    public final static String ORDER_TIME_CHARGE_POINT = "point"; // 导航目标点

    public final static String ROBOT_AUTO_REGISTER_PREFIX = "robot_auto_register_";

    public final static String START = "开始";//开始
    public final static String STOP = "结束";//结束

    //Session 中存储的场景（Scene 场景常量标识）
    public static final String SCENE_SESSION_TAG = "scene_session_information";

    public static final String SESSION_STATION_ID = "stationId";

    //添加初始密码 默认
    public static final String PRIMARY_PWD = "0000";

    //默认机器人类型
    public static final Integer ROBOT_PRIMARY_TYPE_ID = 1;
}
