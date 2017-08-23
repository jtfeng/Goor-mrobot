package cn.muye.base.websoket;

import cn.mrobot.bean.websocket.WSMessage;
import cn.muye.base.cache.CacheInfoManager;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by enva on 17/07/20.
 */
@Slf4j
@Component
public class WebSocketSendMessage {

    /**
     * webSocket发送消息方法，向web页面推送消息
     *
     * @param wsMessage
     * @return
     * @throws Exception
     */
    public boolean sendWebSocketMessage(WSMessage wsMessage) throws Exception {
        String userId = wsMessage.getUserId();
        Session session = CacheInfoManager.getWebSocketSessionCache(userId);
        if (null != session) {
            sendMessage(session, JSON.toJSONString(wsMessage));
        } else {
            sendAll(JSON.toJSONString(wsMessage));
        }
        return false;
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("websocket 发送消息出错", e);
        }
    }

    /**
     * 向所有用户发送
     *
     * @param message
     */
    private void sendAll(String message) {
        Map<String, Session> sessionMap = CacheInfoManager.getWebSocketSessionCache();
        Iterator iterator = sessionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Session session = null;
            try {
                Map.Entry<String, Session> entry = (Map.Entry<String, Session>) iterator.next();
                session = entry.getValue();
                sendMessage(session, message);
            } catch (Exception e) {
                log.debug("群发消息异常", e);
                CacheInfoManager.removeWebSocketSessionCache(session);
                try {
                    session.close();
                } catch (IOException e1) {
                    log.error("websocket session 关闭异常", e);
                }
            }
        }
    }

}
