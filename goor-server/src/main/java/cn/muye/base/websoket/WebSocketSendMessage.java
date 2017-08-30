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
import java.util.Set;

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
     * @throws Exception
     */
    public void sendWebSocketMessage(WSMessage wsMessage) throws Exception {
        String deviceId = wsMessage.getDeviceId();
        //根据机器人code或者站编号 获取session
        Set<Session> sessionSet = CacheInfoManager.getWebSocketSessionCache(deviceId);
        if (sessionSet == null)
            return;
        for (Session session : sessionSet) {
            if (CacheInfoManager.isWebSocketClientReceiveModule(session,wsMessage.getDeviceId(), wsMessage.getModule())) {
                sendWebSocketMessage(wsMessage, session);
            } else {
                log.info(wsMessage.getDeviceId() + "停止接收(未设置接收) " + wsMessage.getModule() + " 信息");
            }
        }
    }

    /**
     * webSocket发送消息方法，向web页面推送消息
     *
     * @param wsMessage
     * @throws Exception
     */
    protected void sendWebSocketMessage(WSMessage wsMessage, Session session) throws Exception {
        if (null != session) {
            sendMessage(session, JSON.toJSONString(wsMessage));
        } else {
            sendAll(JSON.toJSONString(wsMessage));
        }
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
        Map<String, Set<Session>> sessionMap = CacheInfoManager.getWebSocketSessionCache();
        Iterator iterator = sessionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Session session = null;
            try {
                Map.Entry<String, Set<Session>> entry = (Map.Entry<String, Set<Session>>) iterator.next();
                Set<Session> sessionSet = entry.getValue();
                Iterator sessionIterator = sessionSet.iterator();
                while (sessionIterator.hasNext()) {
                    session = (Session) sessionIterator.next();
                    sendMessage(session, message);
                }
            } catch (Exception e) {
                log.debug("群发消息异常", e);
                CacheInfoManager.removeWebSocketSessionCache(session);
                try {
                    if (null != session)
                        session.close();
                } catch (IOException e1) {
                    log.error("websocket session 关闭异常", e);
                }
            }
        }
    }
}
