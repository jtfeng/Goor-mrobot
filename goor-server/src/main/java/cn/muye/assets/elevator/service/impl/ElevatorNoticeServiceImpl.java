package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.station.service.ElevatorstationElevatorXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.service.ElevatorNoticeService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.consumer.ConsumerCommon;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.service.consumer.topic.BaseMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            elevatorNotice.setToStationId(station.getId());
            save(elevatorNotice);
            checkAndSendElevatorNotice(elevatorNotice);
        }
    }

    private boolean checkAndSendElevatorNotice(ElevatorNotice elevatorNotice) {
        logger.info("消息未接收，重新向电梯pad发送websocket消息，elevatorNotice=" + JSON.toJSONString(elevatorNotice));
        boolean sendSuccess = false;  //pad端是否收到消息
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
}
