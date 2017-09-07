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
     * 从cache获取当前切换的场景PC端
     * @return
     * @throws Exception
     */
    public static Scene getScene() throws Exception {
        return SCENE_LOADING_CACHE.getIfPresent(UserUtil.getUserTokenValue()+":"+Constant.SCENE_SESSION_TAG_PC);
    }

}
