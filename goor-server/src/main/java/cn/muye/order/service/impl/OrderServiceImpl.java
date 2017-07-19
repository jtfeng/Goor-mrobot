package cn.muye.order.service.impl;

import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.service.missiontask.MissionFuncsService;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.order.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderServiceImpl extends BasePreInject<Order> implements OrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private RobotService robotService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private MissionFuncsService missionFuncsService;

    @Override
    public void saveOrder(Order order) {
        //保存订单
        preInject(order);
        order.setStatus(OrderConstant.ORDER_STATUS_UNDONE);
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_UNDONE);
            orderDetailService.save(orderDetail);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
        //在这里调用任务生成器
        Order sqlOrder = getOrder(order.getId());
        missionFuncsService.createMissionLists(sqlOrder);
    }

    @Override
    public Order getOrder(Long id) {
        Order getOrder = orderMapper.getById(id);
        if(getOrder != null){
            getOrder.setRobot(robotService.getById(getOrder.getRobot().getId()));
            getOrder.setDetailList(orderDetailService.listOrderDetailByOrderId(getOrder.getId()));
            getOrder.setOrderSetting(orderSettingService.getById(getOrder.getOrderSetting().getId()));
            if(getOrder.getNeedShelf()){
                getOrder.setShelf(shelfService.getById(getOrder.getShelf().getId()));
            }
        }
        return getOrder;
    }
}
