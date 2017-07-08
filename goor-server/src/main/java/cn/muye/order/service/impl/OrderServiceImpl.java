package cn.muye.order.service.impl;

import cn.mrobot.bean.order.Order;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderServiceImpl extends BasePreInject<Order> implements OrderService{

    @Autowired
    private OrderMapper orderMapper;
}
