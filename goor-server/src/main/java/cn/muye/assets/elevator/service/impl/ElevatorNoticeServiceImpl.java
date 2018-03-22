package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.task.JsonElevatorNotice;
import cn.mrobot.bean.order.GoodsInfo;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.station.service.ElevatorstationElevatorXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.area.station.service.StationStationXREFService;
import cn.muye.assets.elevator.mapper.ElevatorNoticeMapper;
import cn.muye.assets.elevator.service.ElevatorNoticeService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.service.OrderDetailService;
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
import java.util.Map;
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
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private StationStationXREFService stationStationXREFService;

    private ReentrantLock lock = new ReentrantLock();
    private ReentrantLock removeLock = new ReentrantLock();

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
                    msg = localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_service_impl_ElevatorNoticeServiceImpl_java_DTZ") + station.getName() + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_service_impl_ElevatorNoticeServiceImpl_java_WDL");
                } else {
                    msg = localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_service_impl_ElevatorNoticeServiceImpl_java_WZYY");
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
        logger.info("向pad发送websocket消息，elevatorNotice=" + JSON.toJSONString(elevatorNotice));
        //发送电梯消息
        if (ElevatorNotice.Type.ELEVATOR_NOTICE.getCode() == elevatorNotice.getType()) {
            sendElevatorNotice(elevatorNotice);
            //发送到站消息
        } else if (ElevatorNotice.Type.ARRIVAL_STATION_NOTICE.getCode() == elevatorNotice.getType()) {
            sendArrivalStationNotice(elevatorNotice);
        }
    }

    /**
     * 发送电梯PAD消息
     *
     * @param elevatorNotice
     */
    private void sendElevatorNotice(ElevatorNotice elevatorNotice) {
        Long elevatorId = elevatorNotice.getElevatorId();
        List<Station> stationList = elevatorstationElevatorXREFService.findByElevator(elevatorId);
        sendElevatorMessage(stationList, elevatorNotice);
    }

    private void sendElevatorMessage(List<Station> stationList, ElevatorNotice elevatorNotice) {
        if (stationList.isEmpty()) {
            logger.info("消息通知发送websocket消息的站列表为空");
            return;
        }
        for (Station station : stationList) {
            Long toStationId = station.getId();
            elevatorNotice = checkOrSaveNotice(elevatorNotice, toStationId);
            checkAndSendElevatorNotice(elevatorNotice);
        }
    }

    /**
     * 消息保存之前先根据UUID和toStationId过滤，如果数据库已经有该UUID的消息,则不存库
     *
     * @param elevatorNotice
     * @param toStationId
     * @return
     */
    private ElevatorNotice checkOrSaveNotice(ElevatorNotice elevatorNotice, Long toStationId) {
        try {
            lock.lock();
            ElevatorNotice elevatorNoticeDB = findByUUIDAndToStation(elevatorNotice.getUuid(), toStationId);
            if (null != elevatorNoticeDB) {
                return elevatorNoticeDB;
            }
            elevatorNotice.setToStationId(toStationId);
            save(elevatorNotice);
        } finally {
            lock.unlock();
        }
        return elevatorNotice;
    }

    private boolean checkAndSendElevatorNotice(ElevatorNotice elevatorNotice) {
        logger.info("消息未接收，重新向电梯pad发送websocket消息，elevatorNotice=" + JSON.toJSONString(elevatorNotice));
        boolean sendSuccess = false;  //pad端是否收到消息
        ////如果是电梯站，根据电梯ID查询未处理的消息通知，将除当前消息之外的全置为已处理，因为同一个电梯同一时间只能过一个机器人，
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
        for (ElevatorNotice elevatorNoticeDB : elevatorNoticeList) {
            if (!elevatorNoticeDB.getId().equals(elevatorNotice.getId())) {
                elevatorNoticeDB.setState(ElevatorNotice.State.RECEIVED.getCode());
                elevatorNoticeMapper.updateByPrimaryKeySelective(elevatorNoticeDB);
                //从缓存中移除该条数据
                CacheInfoManager.removeElevatorNoticeCache(elevatorNoticeDB.getId());
            }
        }
    }

    private void sendWebSocketSendMessage(ElevatorNotice elevatorNotice) {
        WSMessage ws = new WSMessage.Builder().
                title(localeMessageSourceService.getMessage(LogType.ELEVATOR_NOTICE.getValue()))
                .messageType(WSMessageType.NOTIFICATION)
                .body(elevatorNotice)
                .deviceId(elevatorNotice.getToStationId() + "")
                .module(LogType.ELEVATOR_NOTICE.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(ws);
    }

    private void sendWebSocketSendMessage(Long toStationId, List<ElevatorNotice> elevatorNoticeList) {
        WSMessage ws = new WSMessage.Builder().
                title(localeMessageSourceService.getMessage(LogType.ELEVATOR_NOTICE.getValue()))
                .messageType(WSMessageType.NOTIFICATION)
                .body(elevatorNoticeList)
                .deviceId(toStationId + "")
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

    @Override
    public boolean hasLastRobotElevatorNotice(String code) {
        Example example = new Example(ElevatorNotice.class);
        example.createCriteria().andEqualTo("robotCode", code)
                .andEqualTo("state", ElevatorNotice.State.INIT.getCode());
        return elevatorNoticeMapper.selectCountByExample(example) != 0 ? true : false;
    }

    @Override
    public void sendArrivalStationNoticeCache() {
        Map<Long, List<ElevatorNotice>> elevatorNoticeMap = getAllArrivalStationNoticeCache();
        for (Map.Entry entry : elevatorNoticeMap.entrySet()) {
            Long toStationId = (Long) entry.getKey();
            List<ElevatorNotice> elevatorNoticeList = (List<ElevatorNotice>) entry.getValue();
            sendWebSocketSendMessage(toStationId, elevatorNoticeList);
        }
    }

    @Override
    public void updateState(Long id, ElevatorNotice.State state) {
        elevatorNoticeMapper.updateState(id, state.getCode());
    }

    @Override
    public List<ElevatorNotice> listElevatorNotice(Long stationId, Integer state, Integer type) {
        Example example = new Example(ElevatorNotice.class);
        Example.Criteria criteria = example.createCriteria();
        if (null != stationId){
            criteria.andCondition("TO_STATION_ID=" + stationId);
        }
        if (null != state){
            criteria.andCondition("STATE=" + state);
        }
        if (null != type){
            criteria.andCondition("TYPE=" + type);
        }
        example.setOrderByClause("CREATE_TIME ASC");
        return elevatorNoticeMapper.selectByExample(example);
    }

    @Override
    public void removeArrivalStationNoticeCacheByOrderDetailId(Long orderDetailId) {
        try{
            removeLock.lock();
            CacheInfoManager.removeArrivalStationNoticeCacheByOrderDetailId(orderDetailId);
        }finally {
            removeLock.unlock();
        }
    }

    @Override
    public List<ElevatorNotice> getArrivalStationNoticeCache(Long stationId) {
        try{
            removeLock.lock();
            return CacheInfoManager.getArrivalStationNoticeCache(stationId);
        }finally {
            removeLock.unlock();
        }
    }

    @Override
    public void setArrivalStationNoticeCache(Long stationId, ElevatorNotice elevatorNotice) {
        try{
            removeLock.lock();
            CacheInfoManager.setArrivalStationNoticeCache(stationId, elevatorNotice);
        }finally {
            removeLock.unlock();
        }
    }

    @Override
    public Map<Long, List<ElevatorNotice>> getAllArrivalStationNoticeCache() {
        try{
            removeLock.lock();
            return CacheInfoManager.getAllArrivalStationNoticeCache();
        }finally {
            removeLock.unlock();
        }
    }

    /**
     * 发送到站消息
     *
     * @param elevatorNotice
     */
    private void sendArrivalStationNotice(ElevatorNotice elevatorNotice) {
        //根据orderDetail 获取到站的ID，根据ID获取接收消息站的ID，和获取物品名称，数量和单位
        Long orderDetailId = elevatorNotice.getOrderDetailId();
        OrderDetail orderDetail = orderDetailService.getOrderDetailInfo(orderDetailId);
        if (null == orderDetail){
            return;
        }
        //获取当前到站
        Long currentArrivalStationId = orderDetail.getStationId();
        //根据当前到站查询出该站的收信息的站
        List<Station> stationList = stationStationXREFService.getReceiveNoticeStationList(currentArrivalStationId);
        List<GoodsInfo> goodsInfoList = orderDetail.getGoodsInfoList();
        elevatorNotice.setData(JSON.toJSONString(goodsInfoList));
        //将elevatorNotice添加到缓存列表，每次像前端发送列表
        for (Station station : stationList) {
            Long toStationId = station.getId();
            elevatorNotice.setToStationId(toStationId);
            elevatorNotice = checkOrSaveNotice(elevatorNotice, toStationId);
            if (ElevatorNotice.State.RECEIVED.getCode() != elevatorNotice.getState()) {
                setArrivalStationNoticeCache(toStationId, elevatorNotice);
            }
            List<ElevatorNotice> elevatorNoticeList = getArrivalStationNoticeCache(toStationId);
            sendWebSocketSendMessage(toStationId, elevatorNoticeList);
        }
    }
}
