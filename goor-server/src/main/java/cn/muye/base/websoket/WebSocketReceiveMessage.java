package cn.muye.base.websoket;

import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * Created by enva on 17/07/20.
 */
@Component
@Slf4j
public class WebSocketReceiveMessage {

    /**
     * 接收webSocket客户端发过来的消息
     *
     * @param receiveMessage
     * @throws Exception
     */
    public void receiveWebSocketMessage(String receiveMessage, Session session) throws Exception {
        if (StringUtil.isEmpty(receiveMessage)) {
            return;
        }
        //TODO 处理具体业务逻辑,特殊情况下走此方法，一般情况下请走http访问接口的方式，不要走此方法，统一访问入口
        if (StringUtil.isJSON(receiveMessage)) {
            WSMessage wsMessage = WSMessage.parse(receiveMessage);
            if (null == wsMessage)
                return;
            WSMessageType wsMessageType = wsMessage.getMessageType();
            switch (wsMessageType) {
                case REGISTER:
                    handleRegister(wsMessage, session);
                case POSE:
                    break;
                case WARNING:
                    break;
                case NOTIFICATION:
                    break;
                case STOP_SENDING:
                    handleStopSending(wsMessage);
                    break;
            }
        }
    }

    /**
     * 处理注册信息
     *
     * @param wsMessage
     */
    private void handleRegister(WSMessage wsMessage, Session session) {
        String userId = wsMessage.getUserId();
        userId = userId != null ? userId :session.getId();
        CacheInfoManager.setWebSocketSessionCache(userId, session);
    }

    /**
     * 处理停止发送业务
     *
     * @param wsMessage
     */
    private void handleStopSending(WSMessage wsMessage) {
        String module = wsMessage.getModule();
        LogType logType = LogType.getLogType(module);
        if (logType == null)
            return;
        switch (logType) {
            case WARNING_LOWER_POWER:
                stopSendingLowerMessage(logType, wsMessage);
                break;
            default:
                break;
        }
    }

    private void stopSendingLowerMessage(LogType logType, WSMessage wsMessage) {
        CacheInfoManager.setStopSendWebSocketDevice(logType, wsMessage.getDeviceId());
    }
}
