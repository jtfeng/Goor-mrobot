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

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketInit> webSocketSets = new CopyOnWriteArraySet<WebSocketInit>();
    private static final String HEART = "heart";

    private static ApplicationContext applicationContext;
    private WebSocketReceiveMessage webSocketReceiveMessage;
    private Session session;
    private WebSocketSendMessage webSocketSendMessage;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.session = session;
        webSocketSets.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        webSocketSets.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.error("webSocket onClose");
        if (null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()) {
            log.error("ws onClose error, get userName is null");
            return;
        }
        CacheInfoManager.removeWebSocketSessionCache(session.getUserPrincipal().getName());
        log.info("close a connect, current connect count =" + CacheInfoManager.getWebSocketSessionCacheSize());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("receive webSocket client message:" + message);
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
            webSocketReceiveMessage.receiveWebSocketMessage(message);
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
        CacheInfoManager.removeWebSocketSessionCache(session.getUserPrincipal().getName());
        log.info("onError close a connect, current connect count =" + CacheInfoManager.getWebSocketSessionCacheSize());
    }

    /**
     * 向所有用户发送
     *
     * @param message
     */
    public void sendAll(String message) {
        for (WebSocketInit webSocketInit : webSocketSets) {
            try {
                webSocketInit.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.debug("群发消息异常", e);
                webSocketSets.remove(webSocketInit);
                try {
                    webSocketInit.session.close();
                } catch (IOException e1) {
                    log.error("websocket session 关闭异常", e);
                }
            }
        }

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


