package cn.mrobot.bean.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class Constant {

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

    public static final String AUTHORIZE_USERNAME = "web";

    public static final String AUTHORIZE_PASSWORD = "web_secret";

    public static final int ERROR_CODE_NOT_AUTHORIZED = 40001; //未授权

    public static final int ERROR_CODE_NOT_LOGGED = 40002; //未登录

    //地图文件场景下的点和地图路径名
    public final static String MAP_FILE_PATH = "map"; // 地图
    public final static String POINT_FILE_PATH = "point"; // 导航目标点

    public final static String AES_KEY = "trOmVF7Uaqd4h9AN"; //自助注册AES校验码

    public final static String ROBOT_CACHE_KEY = "robotInfo"; //自动注册机器人缓存KEY

    public final static Long CHECK_IF_OFFLINE_TIME = 300000L; //5分钟=30000毫秒

    //与地图点相关的指令列表
    public final static List<Long> ORDER_MAP_POINT_RELATE_LIST = Arrays.asList(
            1L,//单点导航
            4L//
    );

    public final static String ROBOT_BATTERY_THRESHOLD = "local.robot.batteryThreshold"; //机器人电量阈值配置的Key
    public final static long ORDER_WAIT_ID = 6L;//等待任务ID

    public final static String ROBOT_AUTO_REGISTER_PREFIX = "robot_auto_register_";

    //Session 中存储的场景（Scene 场景常量标识）
    public static final String SCENE_SESSION_TAG = "scene_session_information";
}
