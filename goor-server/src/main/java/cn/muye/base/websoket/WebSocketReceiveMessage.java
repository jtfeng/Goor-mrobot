package cn.muye.base.websoket;

import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
public class WebSocketReceiveMessage {

    /**
     * 接收webSocket客户端发过来的消息
     * @param userName
     * @param receiveMessage
     * @throws Exception
     */
    public void receiveWebSocketMessage(String userName, String receiveMessage) throws Exception {
        if(StringUtils.isEmpty(userName) || StringUtil.isEmpty(receiveMessage)){
            return;
        }
        Session session = CacheInfoManager.getWebSocketSessionCache(userName);

        //TODO 处理具体业务逻辑,特殊情况下走此方法，一般情况下请走http访问接口的方式，不要走此方法，统一访问入口

    }


}
