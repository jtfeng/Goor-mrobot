package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.mission.task.JsonElevatorNotice;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2018/1/8
 */
public interface ElevatorNoticeService extends BaseService<ElevatorNotice> {

    void sendElevatorNoticeToX86(ElevatorNotice elevatorNotice, int code, String deviceId, String msg);

    void sendElevatorNoticeToWebSocket(ElevatorNotice elevatorNotice);

    void sendElevatorNoticeCache();

    ElevatorNotice findByUUIDAndToStation(String uuid, Long toStationId);

    void updateStateByMissionItemData(String data, int state);

    ElevatorNotice selectByData(JsonElevatorNotice jsonElevatorNotice, int state);

    List<ElevatorNotice> findByElevatorId(Long elevatorId, int state);

    boolean hasLastRobotElevatorNotice(String code);
}
