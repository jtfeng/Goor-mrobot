package cn.muye.base.bean;

import edu.wpi.rail.jrosbridge.handler.RosHandler;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;

/**
 * Created by enva on 2017/8/8.
 */
@Slf4j
public class RosHandlerImp implements RosHandler {
    @Override
    public void handleConnection(Session session) {
        log.info("----------->>>>>>>>>>>>>>handleConnection");
    }

    @Override
    public void handleDisconnection(Session session) {
        log.info("----------->>>>>>>>>>>>>>handleDisconnection");
    }

    @Override
    public void handleError(Session session, Throwable throwable) {
        log.info("----------->>>>>>>>>>>>>>handleError");
    }
}
