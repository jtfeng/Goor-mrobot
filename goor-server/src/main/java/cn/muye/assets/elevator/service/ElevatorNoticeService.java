package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.websocket.WSMessage;
import cn.muye.base.service.BaseService;

/**
 *
 * @author Jelynn
 * @date 2018/1/8
 */
public interface ElevatorNoticeService extends BaseService<ElevatorNotice> {

    void sendElevatorNoticeToX86(ElevatorNotice elevatorNotice, int code, String deviceId, String msg);

    void sendElevatorNoticeToWebSocket(ElevatorNotice elevatorNotice);

    void sendElevatorNoticeCache();

}
