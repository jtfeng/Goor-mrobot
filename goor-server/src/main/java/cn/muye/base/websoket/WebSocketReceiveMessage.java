package cn.muye.base.websoket;

import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * Created by enva on 17/07/20.
 */
@Component
@Slf4j
public class WebSocketReceiveMessage {

    @Autowired
    private WebSocketSendMessage webSocketSendMessage; //暂时这样做

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
                    handleSpecificType(wsMessage);
                    break;
                case POSE:
                    break;
                case WARNING:
                    break;
                case NOTIFICATION:
                    break;
//                case SPECIFIC_TYPE:
//                    handleSpecificType(wsMessage);
//                    break;
                case STOP_SENDING:
                    handleStopSending(wsMessage);
                    break;
            }
        }
    }

    /**
     * 处理客户端在指定消息类型请求
     *
     * @param wsMessage 消息内容
     */
    private void handleSpecificType(WSMessage wsMessage) {
        String moduleStr = wsMessage.getModule();
        String[] modules = splitStr(moduleStr);
        String userIdStr = wsMessage.getUserId();
        String[] userIds = splitStr(userIdStr);
        for (String module : modules){
            for (String userId : userIds){
                CacheInfoManager.setSpecificTypeDeviceId(userId, module);
            }
        }
    }

    /**
     * 处理注册信息
     *
     * @param wsMessage
     */
    private void handleRegister(WSMessage wsMessage, Session session) throws Exception {
        String userIdStr = wsMessage.getUserId();
        String[] userIds = splitStr(userIdStr);
        if (userIds.length > 0) {
            for (String userId : userIds) {
                if (!StringUtil.isNullOrEmpty(userId)) {
                    CacheInfoManager.setWebSocketSessionCache(userId, session);
                }
            }
            wsMessage.setBody(true);
            webSocketSendMessage.sendWebSocketMessage(wsMessage, session);
        } else {
            //推送前端注册失败，userId为空
            wsMessage.setBody(false);
            webSocketSendMessage.sendWebSocketMessage(wsMessage);
        }
    }

    /**
     * 处理停止发送业务
     *
     * @param wsMessage
     */
    private void handleStopSending(WSMessage wsMessage) {
        String userIdStr = wsMessage.getUserId();
        String[] userIds = splitStr(userIdStr);
        for (String userId : userIds) {
            if (!StringUtil.isNullOrEmpty(userId))
                CacheInfoManager.setStopSendWebSocketDevice(wsMessage.getModule(), userId);
        }
    }

    private String[] splitStr(String str) {
        if (!StringUtil.isNullOrEmpty(str))
            return str.split(",");
        return new String[0];
    }
}
