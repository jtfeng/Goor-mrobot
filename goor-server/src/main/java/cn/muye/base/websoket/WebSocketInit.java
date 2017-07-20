package cn.muye.base.websoket;

import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by enva on 17/07/20.
 */
@CrossOrigin
@ServerEndpoint(value = "/ws")
@Component
@Slf4j
public class WebSocketInit implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private WebSocketReceiveMessage webSocketReceiveMessage;

    private WebSocketSendMessage webSocketSendMessage;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) throws Exception {
        if(null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()){
            log.error("ws connect error, get userName is null");
            throw new Exception();
        }
        CacheInfoManager.setWebSocketSessionCache(session.getUserPrincipal().getName(), session);

        //以下为测试用
//        webSocketSendMessage = applicationContext.getBean(WebSocketSendMessage.class);
//        CacheInfoManager.setWebSocketSessionCache("1", session);
//        try {
//            if(!webSocketSendMessage.sendWebSocketMessage(session.getUserPrincipal().getName(), "0")){
//                log.error("ws send error");
//                throw new Exception();
//            }
//        } catch (IOException e) {
//            log.error("open ws error", e);
//            throw new Exception();
//        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session)  {
        log.error("webSocket onClose");
        if(null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()){
            log.error("ws onClose error, get userName is null");
            return;
        }
        CacheInfoManager.removeWebSocketSessionCache(session.getUserPrincipal().getName());
        log.info("close a connect, current connect count ="+ CacheInfoManager.getWebSocketSessionCacheSize() );
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session)  {
        log.info("receive webSocket client message:" + message);
        if(null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()){
            log.error("ws onMessage error, get userName is null");
            return;
        }
        try {
            if(null == applicationContext){
                return;
            }
            webSocketReceiveMessage = applicationContext.getBean(WebSocketReceiveMessage.class);
            if(null == webSocketReceiveMessage){
                return;
            }
            webSocketReceiveMessage.receiveWebSocketMessage(session.getUserPrincipal().getName(), message);
//            webSocketReceiveMessage.receiveWebSocketMessage("1", message);//测试用
        }catch (Exception e){
            log.error("receive message exception", e);
        }
    }

    /**
     * 发生错误时调用
     @OnError
     */
    @OnError
    public void onError(Session session, Throwable error)  {
        log.error("webSocket error", error);
        if(null == session || null == session.getUserPrincipal() || null == session.getUserPrincipal().getName()){
            log.error("ws onError error, get userName is null");
            return;
        }
        CacheInfoManager.removeWebSocketSessionCache(session.getUserPrincipal().getName());
        log.info("onError close a connect, current connect count ="+ CacheInfoManager.getWebSocketSessionCacheSize() );
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketInit.applicationContext = applicationContext;
    }
}
