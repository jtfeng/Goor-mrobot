package cn.muye.util;

import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chay on 2017/7/19.
 */
public class SessionUtil {

    /**
     * 从session获取当前切换的场景
     * @param request
     * @return
     * @throws Exception
     */
    public static Scene getScene(HttpServletRequest request) throws Exception {
        Object o = request.getSession().getAttribute(Constant.SCENE_SESSION_TAG);
        if(o == null) {
            return null;
        }
        return (Scene) o;
    }

}
