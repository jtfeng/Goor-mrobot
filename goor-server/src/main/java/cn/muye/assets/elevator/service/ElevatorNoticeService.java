package cn.muye.assets.elevator.service;

import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.mission.task.JsonElevatorNotice;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Jelynn
 * @date 2018/1/8
 */
public interface ElevatorNoticeService extends BaseService<ElevatorNotice> {

    void sendElevatorNoticeToX86(ElevatorNotice elevatorNotice, int code, String deviceId, String msg);

    /**
     * 2018-3-26 取消websocket发送，改成前端5s/次请求接口
     * @param elevatorNotice
     */
    void sendElevatorNoticeToWebSocket(ElevatorNotice elevatorNotice);

    ElevatorNotice findByUUIDAndToStation(String uuid, Long toStationId);

    void updateStateByMissionItemData(String data, int state);

    ElevatorNotice selectByData(JsonElevatorNotice jsonElevatorNotice, int state);

    List<ElevatorNotice> findByElevatorId(Long elevatorId, int state);

    boolean hasLastRobotElevatorNotice(String code);

    void updateState(Long id, ElevatorNotice.State state);

    List<ElevatorNotice> listElevatorNotice(Long stationId, Integer state, Integer type);

    /**
     * 以下四个方法对楼层管家消息缓存进行封装，避免出现多线程下的
     * ConcurrentModificationException
     * @param orderDetailId
     */
    void removeArrivalStationNoticeCacheByOrderDetailId(Long orderDetailId);

    List<ElevatorNotice> getArrivalStationNoticeCache(Long stationId);

    void setArrivalStationNoticeCache(Long stationId, ElevatorNotice elevatorNotice);
}
