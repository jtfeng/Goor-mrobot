package cn.muye.order.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.order.bean.WSOrderNotificationType;
import cn.muye.order.bean.WSOrderNotificationVO;
import cn.muye.order.mapper.OrderDetailMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private WebSocketSendMessage webSocketSendMessage;
    @Autowired
    private StationService stationService;

    @Override
    public List<OrderDetail> listOrderDetailByOrderId(Long orderId) {
        return orderDetailMapper.listOrderDetailByOrderId(orderId);
    }

    @Override
    public void finishedDetailTask(Long id, Integer type) {
        OrderDetail orderDetail  = new OrderDetail(id);
        if(type == OrderConstant.ORDER_DETAIL_STATUS_GET){
            //已到达，通知推送
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_GET);
            OrderDetail sqlDetail = super.findById(id);
            Station station = stationService.findById(sqlDetail.getStationId());
            Order sqlOrder = orderService.getOrder(sqlDetail.getOrderId());
            String receiveBody = "已送达站" + station.getName();
            String sendBody = "已送达站" + station.getName();

            WSMessage wsReceive = new WSMessage.Builder().
                    title(LogType.INFO_GOAL_REACHED.getValue())
                    .messageType(WSMessageType.NOTIFICATION)
                    .body(new WSOrderNotificationVO(WSOrderNotificationType.RECEIVE_STATION, receiveBody))
                    .deviceId(sqlDetail.getStationId()+"")
                    .module(LogType.INFO_GOAL_REACHED.getName()).build();
            WSMessage wsSend = new WSMessage.Builder().
                    title(LogType.INFO_GOAL_REACHED.getValue())
                    .messageType(WSMessageType.NOTIFICATION)
                    .body(new WSOrderNotificationVO(WSOrderNotificationType.SEND_STATION, sendBody))
                    .deviceId(sqlOrder.getStartStation().getId() + "")
                    .module(LogType.INFO_GOAL_REACHED.getName()).build();
            try {
                webSocketSendMessage.sendWebSocketMessage(wsReceive);
                webSocketSendMessage.sendWebSocketMessage(wsSend);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

        }else if(type == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_SIGN);
        }else {
            return;
        }
        orderDetail.setFinishDate(new Date());
        updateSelective(orderDetail);
        //更新后检测order 状态
        OrderDetail getOrderDetail = super.findById(id);
        int undoneCount = orderDetailMapper.countUndoneDetail(getOrderDetail.getOrderId());
        if(undoneCount == 0){
            Order order = orderMapper.getById(getOrderDetail.getOrderId());
            order.setStatus(OrderConstant.ORDER_STATUS_DONE);
            orderMapper.updateOrder(order);
            //修改完订单状态后 再次修改机器人的状态
            Robot robot = new Robot();
            robot.setId(order.getRobot().getId());
            robot.setBusy(Boolean.FALSE);
            robotService.updateSelective(robot);
        }
    }

    @Override
    public List<OrderDetail> listStationTasks(Long stationId, WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        return orderDetailMapper.listStationTasks(stationId);
    }

    @Override
    public OrderDetail getOrderDetailInfo(Long id) {
        return orderDetailMapper.getOrderDetailInfo(id);
    }

}
