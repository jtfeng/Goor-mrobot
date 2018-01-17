package cn.mrobot.bean.constant;

import cn.mrobot.bean.version.RequestData;
import com.alibaba.fastjson.JSON;

/**
 *
 * @author Jelynn
 * @date 2018/1/10
 */
public class VersionConstants {

    //版本信息
    public final static String VERSION_NOAH_GOOR_SERVER_KEY = "goorServerVersion";//服务端键名
    public final static String VERSION_NOAH_GOOR_SERVER = "NOAH_GoorServer_Ver0.0.0.1015hospital_test";//服务端：NOAH_GoorServer_Ver0.0.0.1012_test
    public final static String VERSION_NOAH_GOOR_KEY = "goor_version";//agent端键名
    public final static String VERSION_NOAH_GOOR = "NOAH_Goor_Ver0.0.0.1007_test";//agent端：NOAH_Goor_Ver0.0.0.1007_test
    public final static String getVersionNoahGoorJSON () {
        return JSON.toJSONString(new RequestData(VERSION_NOAH_GOOR_KEY, "{\"" + VERSION_NOAH_GOOR_KEY + "\":\"" + VERSION_NOAH_GOOR+ "\"}"));
    }
    //20180110暂时只能用数字
//    public final static String VERSION_NOAH_GOOR = "10001007";//agent端：NOAH_Goor_Ver0.0.0.1007_test

}
