package cn.muye.util;

import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Created by chay on 2017/7/19.
 */
public class SessionUtil {

    /**
     * 定义一个存放场景信息的 Cache，Key 的结构为 ： token + ":" + 常量标识符
     */
    public static final Cache<String, Scene> SCENE_LOADING_CACHE = CacheBuilder.newBuilder().maximumSize(100)
            .expireAfterWrite(123, TimeUnit.MINUTES).build();

    /**
     * 从session获取当前切换的场景
     * @param request
     * @return
     * @throws Exception
     */
    public static Scene getScene(HttpServletRequest request) throws Exception {
         return SCENE_LOADING_CACHE.getIfPresent(UserUtil.getUserTokenValue()+":"+Constant.SCENE_SESSION_TAG);
    }

    /**
     * 从session获取当前stationId
     * @param request
     * @return
     * @throws Exception
     */
    public static Long getStationId(HttpServletRequest request) throws Exception {
        Object o = request.getSession().getAttribute(Constant.SESSION_STATION_ID);
        if(o == null) {
            return null;
        }
        return (Long) o;
    }


}
