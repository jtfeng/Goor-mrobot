package cn.mrobot.bean.constant;

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

}
