package cn.muye.order.service.impl;

import cn.mrobot.bean.order.OrderSetting;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.order.mapper.OrderSettingMapper;
import cn.muye.order.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/6.
 */
@Service
@Transactional
public class OrderSettingServiceImpl extends BasePreInject<OrderSetting> implements OrderSettingService {

    @Autowired
    private OrderSettingMapper orderSettingMapper;

    @Override
    public OrderSetting getById(Long id) {
        return orderSettingMapper.getById(id);
    }

    @Override
    public int saveOrderSetting(OrderSetting orderSetting) {
        preInject(orderSetting);
        return orderSettingMapper.saveOrderSetting(orderSetting);
    }

    @Override
    public int updateOrderSetting(OrderSetting orderSetting) {
        return orderSettingMapper.updateOrderSetting(orderSetting);
    }

    @Override
    public int deleteOrderSetting(Long id) {
        OrderSetting orderSetting = new OrderSetting(id);
        orderSetting.setDeleteStatus(Boolean.TRUE);
        return orderSettingMapper.updateOrderSetting(orderSetting);
    }
}
