package cn.muye.order.service.impl;

import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.muye.base.service.imp.BasePreInject;
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

    @Override
    public void saveOrder(Order order) {
        //保存订单
        preInject(order);
        //Long userId = order.getCreatedBy();
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetailService.save(orderDetail);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
    }

    @Override
    public Order getOrder(Long id) {
        Order getOrder = orderMapper.getById(id);
        if(getOrder != null){
            getOrder.setDetailList(orderDetailService.listOrderDetailByOrderId(getOrder.getId()));
            getOrder.setOrderSetting(orderSettingService.getById(getOrder.getOrderSetting().getId()));
        }
        return getOrder;
    }
}
