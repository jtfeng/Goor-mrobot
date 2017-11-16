package cn.muye.order.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.order.MessageBell;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.mapper.OrderDetailMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.MessageBellService;
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
    private StationService stationService;
    @Autowired
    private MessageBellService messageBellService;

    @Override
    public List<OrderDetail> listOrderDetailByOrderId(Long orderId) {
        List<OrderDetail> orderDetailList = orderDetailMapper.listOrderDetailByOrderId(orderId);
        orderDetailList.forEach(orderDetail -> {
            Station findStation = stationService.findById(orderDetail.getStationId());
            if(findStation!= null){
                orderDetail.setStationName(findStation.getName());
            }
        });
        return orderDetailList;
    }

    @Override
    public void finishedDetailTask(Long id, Integer type) {
        //先查看detail是否已经到达签收情况
        OrderDetail sqlOrderDetail = orderDetailMapper.selectByPrimaryKey(id);
        if(sqlOrderDetail != null){
            if(sqlOrderDetail.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                return;
            }else if(sqlOrderDetail.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                if(type != OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    return;
                }
            }
        }
        //状态需要修改的情况下
        OrderDetail orderDetail  = new OrderDetail(id);
        if(type == OrderConstant.ORDER_DETAIL_STATUS_GET){
            //已到达，通知推送
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_GET);
            OrderDetail sqlDetail = super.findById(id);
            Station station = stationService.findById(sqlDetail.getStationId());
            Order sqlOrder = orderService.getOrder(sqlDetail.getOrderId());
            String receiveBody = "已送达站" + station.getName();
            String sendBody = "已送达站" + station.getName();
            //将推送信息加入数据库内
            MessageBell receiveBell = new MessageBell(receiveBody, sqlOrder.getRobot().getCode(),OrderConstant.MESSAGE_BELL_RECEIVE, sqlDetail.getStationId(), OrderConstant.MESSAGE_BELL_UNREAD);
            messageBellService.save(receiveBell);
            MessageBell sendBell = new MessageBell(sendBody, sqlOrder.getRobot().getCode(),OrderConstant.MESSAGE_BELL_SEND, sqlOrder.getStartStation().getId(), OrderConstant.MESSAGE_BELL_UNREAD);
            messageBellService.save(sendBell);
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
        Date currentDate = new Date();
        long oneDayBefore = currentDate.getTime() - 24*60*60*1000;
        Date beforeDate = new Date(oneDayBefore);
        return orderDetailMapper.listStationTasks(stationId, currentDate, beforeDate);
    }

    @Override
    public OrderDetail getOrderDetailInfo(Long id) {
        return orderDetailMapper.getOrderDetailInfo(id);
    }

}
