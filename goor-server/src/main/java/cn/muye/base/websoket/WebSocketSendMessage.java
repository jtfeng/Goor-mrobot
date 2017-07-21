package cn.muye.base.websoket;

import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by enva on 17/07/20.
 */
@Component
@Slf4j
public class WebSocketSendMessage {

    /**
     * webSocket发送消息方法，向web页面推送消息
     * @param userName
     * @param sendMessage
     * @return
     * @throws Exception
     */
    public boolean sendWebSocketMessage(String userName, String sendMessage) throws Exception {
        if(StringUtils.isEmpty(userName) || StringUtil.isEmpty(sendMessage)){
            return false;
        }
        Session session = CacheInfoManager.getWebSocketSessionCache(userName);
        if(null != session){
            session.getBasicRemote().sendText(sendMessage);
            return true;
        }
        return false;

    }

    /**
     * 返回webSocket连接在线数
     * @return
     */
    public int getWebSocketOnline(){
        return CacheInfoManager.getWebSocketSessionCacheSize();
    }

}
