package cn.muye.base.websoket;

import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by enva on 17/07/20.
 */
@CrossOrigin
@ServerEndpoint(value = "/goor/ws")
@Component
@Slf4j
public class WebSocketInit implements ApplicationContextAware {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    private static final String HEART = "heart";

    private static ApplicationContext applicationContext;
    private WebSocketReceiveMessage webSocketReceiveMessage;
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.session = session;
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        subOnlineCount();           //在线数减1
        log.error("webSocket onClose");
        CacheInfoManager.removeWebSocketSessionCache(session);
        log.info("close a connect, current connect count =" + CacheInfoManager.getWebSocketSessionCacheSize());
        //去除当前设定的机器人接收指定类型信息
        Map<String, Session> sessionMap = CacheInfoManager.getWebSocketSessionCache();
        Iterator iterator = sessionMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Session> entry = (Map.Entry<String, Session>) iterator.next();
            if (entry.getValue().equals(session)){
                String key = entry.getKey();
                CacheInfoManager.removeSpecificTypeDeviceId(key);
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("接收到 webSocket 客户端 message:" + message);
        try {
            //过滤心跳数据
            if (message.indexOf(HEART) >= 0) {
                return;
            }
            if (null == applicationContext) {
                return;
            }
            webSocketReceiveMessage = applicationContext.getBean(WebSocketReceiveMessage.class);
            if (null == webSocketReceiveMessage) {
                return;
            }
            webSocketReceiveMessage.receiveWebSocketMessage(message, session);
        } catch (Exception e) {
            log.error("receive message exception", e);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("webSocket error", error);
        if (null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()) {
            log.error("ws onError error, get userName is null");
            return;
        }
        CacheInfoManager.removeWebSocketSessionCache(session);
        log.info("onError close a connect, current connect count =" + CacheInfoManager.getWebSocketSessionCacheSize());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketInit.applicationContext = applicationContext;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketInit.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketInit.onlineCount--;
    }
}


