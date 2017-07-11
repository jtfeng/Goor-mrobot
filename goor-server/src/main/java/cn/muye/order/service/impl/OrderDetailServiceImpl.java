package cn.muye.order.service.impl;

import cn.mrobot.bean.order.GoodsInfo;
import cn.mrobot.bean.order.OrderDetail;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.mapper.OrderDetailMapper;
import cn.muye.order.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public List<OrderDetail> listOrderDetailByOrderId(Long orderId) {
        return orderDetailMapper.listOrderDetailByOrderId(orderId);
    }
}
