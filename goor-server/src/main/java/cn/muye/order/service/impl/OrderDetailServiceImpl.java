package cn.muye.order.service.impl;

import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.mapper.OrderDetailMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderDetailService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
    private RobotService robotService;

    @Override
    public List<OrderDetail> listOrderDetailByOrderId(Long orderId) {
        return orderDetailMapper.listOrderDetailByOrderId(orderId);
    }

    @Override
    public void finishedDetailTask(Long id, Integer type) {
        OrderDetail orderDetail  = new OrderDetail(id);
        if(type == OrderConstant.ORDER_DETAIL_STATUS_GET){
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_GET);
        }else if(type == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_SIGN);
        }else {
            return;
        }
        orderDetail.setFinishDate(new Date());
        updateSelective(orderDetail);
        //更新后检测order 状态
        /*OrderDetail getOrderDetail = super.findById(id);
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
        }*/
    }

    @Override
    public List<OrderDetail> listStationTasks(Long stationId, WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        Example example = new Example(OrderDetail.class);
        example.createCriteria().andCondition("STATION_ID = ", stationId);
        example.setOrderByClause("STATUS ASC, CREATE_TIME DESC");
        return myMapper.selectByExample(example);
    }

    @Override
    public OrderDetail getOrderDetailInfo(Long id) {
        return orderDetailMapper.getOrderDetailInfo(id);
    }
}
