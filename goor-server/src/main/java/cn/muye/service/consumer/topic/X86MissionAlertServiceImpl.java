package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.log.alert.JsonLogAlert;
import cn.mrobot.bean.log.alert.LogAlert;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.log.alert.service.LogAlertService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.order.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ray.fu on 2017/9/11.
 */
@Service
public class X86MissionAlertServiceImpl implements X86MissionAlertService {

    private Logger logger = Logger.getLogger(X86MissionAlertServiceImpl.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private MissionListTaskService missionListTaskService;

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    LogAlertService logAlertService;

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @Override
    public AjaxResult handleX86MissionAlert(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(messageInfo, new TypeToken<MessageInfo>() {}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)) {
            JsonLogAlert jsonLogAlert = JSON.parseObject(data, JsonLogAlert.class);
            if (jsonLogAlert != null) {
                //设置最新任务报警状态的缓存
                CacheInfoManager.setRobotMissionAlertStatusCache(baseMessageService.getSenderId(messageInfo), String.valueOf(jsonLogAlert.getAlert_code()));
                LogAlert logAlert = new LogAlert();
                String robotCode = baseMessageService.getSenderId(messageInfo);
                Long missionItemId = jsonLogAlert.getMission_item_id();
                String message = jsonLogAlert.getMsg();
                logAlert.setRobotCode(robotCode);
                logAlert.setAlertCode(String.valueOf(jsonLogAlert.getAlert_code()));
                logAlert.setAlertTime(new Date(jsonLogAlert.getAlert_time()));
                logAlert.setDescription(message);
                logAlert.setMissionItemId(missionItemId);
                logAlertService.save(logAlert);
                //TODO 添加websocket推送，将消息推送给起点站平板和后台,因为后台暂时没有发送方案，所以方法暂时注释
//                sendWebSocket(robotCode, missionItemId, message);
            }
        }
        return null;
    }

    private void sendWebSocket(String robotCode, Long missionItemId, String message) {
        Long stationId = getStartStationId(missionItemId);
        sendWebSocketToStartStation(stationId, message);
//        sendWebSocketToServer(robotCode, message);
    }

    private void sendWebSocketToServer(String robotCode, String message) {
        if (StringUtil.isNullOrEmpty(robotCode))
            return;
        WSMessage wsMessage = new WSMessage.Builder().
                title(LogType.WARNING_TIMEOUT.getValue())
                .messageType(WSMessageType.WARNING)
                .body(message)
                .deviceId(robotCode)
                .module(LogType.WARNING_TIMEOUT.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(wsMessage);
    }

    private Long getStartStationId(Long missionItemId) {
        Long orderId = getOrderId(missionItemId);
        Order order = orderService.getOrder(orderId);
        Station station = order != null ? order.getStartStation() : null;
        return station != null ? station.getId() : null;
    }

    private Long getOrderId(Long missionItemId) {
        MissionListTask missionListTask = missionListTaskService.findById(missionItemId);
        return missionListTask != null ? missionListTask.getOrderId() : null;
    }

    private void sendWebSocketToStartStation(Long stationId, String message) {
        if (stationId == null)
            return;
        WSMessage wsMessage = new WSMessage.Builder().
                title(LogType.WARNING_TIMEOUT.getValue())
                .messageType(WSMessageType.WARNING)
                .body(message)
                .deviceId(Long.toString(stationId))
                .module(LogType.WARNING_TIMEOUT.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(wsMessage);
    }
}
