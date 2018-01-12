package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.task.JsonElevatorNotice;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.station.service.ElevatorstationElevatorXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.mapper.ElevatorNoticeMapper;
import cn.muye.assets.elevator.service.ElevatorNoticeService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.service.consumer.topic.BaseMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jelynn
 * @date 2018/1/8
 */
@Service
public class ElevatorNoticeServiceImpl extends BaseServiceImpl<ElevatorNotice> implements ElevatorNoticeService {

    private static final Logger logger = Logger.getLogger(ElevatorNoticeServiceImpl.class);
    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    StationService stationService;

    @Autowired
    private ElevatorstationElevatorXREFService elevatorstationElevatorXREFService;

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @Autowired
    private ElevatorNoticeMapper elevatorNoticeMapper;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void sendElevatorNoticeToX86(ElevatorNotice elevatorNotice, int code, String deviceId, String message) {
        String msg = "success";
        if (StringUtil.isNotBlank(message)) {
            msg = message;
        } else {
            if (TopicConstants.ERROR_CODE_FAIL == code) {
                //如果未成功，需要根据deviceId查询出站，提示未成功原因
                if (StringUtil.isNotBlank(deviceId)) {
                    Station station = stationService.findById(Long.parseLong(deviceId));
                    msg = "电梯站(" + station.getName() + ")未登录";
                } else {
                    msg = "未知原因";
                }
            }
        }
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.PUB_NAME, TopicConstants.ELEVATOR_NOTICE);
        JSONObject dataObject = new JSONObject();
        dataObject.put("callFloor", elevatorNotice.getCallFloor());
        dataObject.put("targetFloor", elevatorNotice.getTargetFloor());
        dataObject.put("elevatorId", elevatorNotice.getElevatorId());
        messageObject.put(TopicConstants.DATA, dataObject);
        messageObject.put("msg", msg);
        messageObject.put("error_code", code);
        messageObject.put(TopicConstants.UUID, elevatorNotice.getUuid());

        baseMessageService.sendRobotMessage(
                elevatorNotice.getRobotCode(),
                TopicConstants.AGENT_PUB,
                JSON.toJSONString(messageObject));
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, rollbackFor = Exception.class)
    @Override
    public void sendElevatorNoticeToWebSocket(ElevatorNotice elevatorNotice) {
        logger.info("向电梯pad发送websocket消息，elevatorNotice=" + JSON.toJSONString(elevatorNotice));
        Long elevatorId = elevatorNotice.getElevatorId();
        //根据电梯获取其绑定的站
        List<Station> stationList = elevatorstationElevatorXREFService.findByElevator(elevatorId);
        if (null == stationList) {
            return;
        }
        for (Station station : stationList) {
            Long toStationId = station.getId();
            //消息保存之前先根据UUID和toStationId过滤，如果数据库已经有该UUID的消息,则不存库
            try{
                lock.lock();
                ElevatorNotice elevatorNoticeDB = findByUUIDAndToStation(elevatorNotice.getUuid(), toStationId);
                if (null != elevatorNoticeDB) {
                    return;
                }
                elevatorNotice.setToStationId(toStationId);
                save(elevatorNotice);
            }finally {
                lock.unlock();
            }
            checkAndSendElevatorNotice(elevatorNotice);
        }
    }

    private boolean checkAndSendElevatorNotice(ElevatorNotice elevatorNotice) {
        logger.info("消息未接收，重新向电梯pad发送websocket消息，elevatorNotice=" + JSON.toJSONString(elevatorNotice));
        boolean sendSuccess = false;  //pad端是否收到消息
        //根据电梯ID查询未处理的消息通知，将除当前消息之外的全置为已处理，因为同一个电梯同一时间只能过一个机器人，
        handleOtherNotices(elevatorNotice);
        //添加缓存
        Long elevatorNoticeId = elevatorNotice.getId();
        CacheInfoManager.setElevatorNoticeCache(elevatorNoticeId);
        //发送之前校验消息是否接收到反馈
        ElevatorNotice elevatorNoticeDB = findById(elevatorNoticeId);
        if (ElevatorNotice.State.RECEIVED.getCode() == elevatorNoticeDB.getState()) {
            sendSuccess = true;
            //发送成功，删除缓存
            CacheInfoManager.removeElevatorNoticeCache(elevatorNoticeId);
        } else {
            sendWebSocketSendMessage(elevatorNotice);
        }
        return sendSuccess;
    }

    private void handleOtherNotices(ElevatorNotice elevatorNotice) {
        List<ElevatorNotice> elevatorNoticeList = findByElevatorId(elevatorNotice.getElevatorId(), ElevatorNotice.State.INIT.getCode());
        for (ElevatorNotice elevatorNoticeDB : elevatorNoticeList){
            if (!elevatorNoticeDB.getId().equals(elevatorNotice.getId())){
                elevatorNoticeDB.setState(ElevatorNotice.State.RECEIVED.getCode());
                elevatorNoticeMapper.updateByPrimaryKeySelective(elevatorNoticeDB);
                //从缓存中移除该条数据
                CacheInfoManager.removeElevatorNoticeCache(elevatorNoticeDB.getId());
            }
        }
    }

    private void sendWebSocketSendMessage(ElevatorNotice elevatorNotice) {
        WSMessage ws = new WSMessage.Builder().
                title(LogType.ELEVATOR_NOTICE.getValue())
                .messageType(WSMessageType.NOTIFICATION)
                .body(elevatorNotice)
                .deviceId(elevatorNotice.getToStationId() + "")
                .module(LogType.ELEVATOR_NOTICE.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(ws);
    }

    @Override
    public void sendElevatorNoticeCache() {
        List<Long> elevatorNoticeIdList = CacheInfoManager.getElevatorNoticeCache();
        if (null != elevatorNoticeIdList && elevatorNoticeIdList.size() > 0) {
            for (Long elevatorNoticeId : elevatorNoticeIdList) {
                ElevatorNotice elevatorNotice = findById(elevatorNoticeId);
                if (null != elevatorNotice) {
                    checkAndSendElevatorNotice(elevatorNotice);
                }
            }
        }
    }

    @Override
    public ElevatorNotice findByUUIDAndToStation(String uuid, Long toStationId) {
        Example example = new Example(ElevatorNotice.class);
        example.createCriteria().andCondition("UUID='" + uuid + "'")
                .andCondition("TO_STATION_ID=" + toStationId);
        List<ElevatorNotice> elevatorNoticeList = elevatorNoticeMapper.selectByExample(example);
        return null != elevatorNoticeList && elevatorNoticeList.size() > 0 ? elevatorNoticeList.get(0) : null;
    }

    @Override
    public void updateStateByMissionItemData(String data, int state) {
        JsonElevatorNotice jsonElevatorNotice = JSON.parseObject(data, JsonElevatorNotice.class);
        //查询出未处理的电梯消息通知
        ElevatorNotice elevatorNotice = selectByData(jsonElevatorNotice, ElevatorNotice.State.INIT.getCode());
        if (null != elevatorNotice) {
            elevatorNotice.setState(ElevatorNotice.State.RECEIVED.getCode());
            elevatorNoticeMapper.updateByPrimaryKeySelective(elevatorNotice);
        }
    }

    @Override
    public ElevatorNotice selectByData(JsonElevatorNotice jsonElevatorNotice, int state) {
        Example example = new Example(ElevatorNotice.class);
        example.createCriteria().andCondition("TARGET_FLOOR=" + jsonElevatorNotice.getTargetFloor())
                .andCondition("ELEVATOR_ID=" + jsonElevatorNotice.getElevatorId())
                .andCondition("CALL_FLOOR=" + jsonElevatorNotice.getCallFloor())
                .andCondition("STATE=" + state);
        List<ElevatorNotice> elevatorNoticeList = elevatorNoticeMapper.selectByExample(example);
        return null != elevatorNoticeList && elevatorNoticeList.size() > 0 ? elevatorNoticeList.get(0) : null;
    }

    @Override
    public List<ElevatorNotice> findByElevatorId(Long elevatorId, int state) {
        Example example = new Example(ElevatorNotice.class);
        example.createCriteria().andCondition("ELEVATOR_ID=" + elevatorId)
                .andCondition("STATE=" + state);
        List<ElevatorNotice> elevatorNoticeList = elevatorNoticeMapper.selectByExample(example);
        return elevatorNoticeList;
    }
}
