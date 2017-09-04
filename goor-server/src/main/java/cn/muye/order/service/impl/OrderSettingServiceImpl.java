package cn.muye.order.service.impl;

import cn.mrobot.bean.order.OrderSetting;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.order.mapper.OrderSettingMapper;
import cn.muye.order.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public int saveOrderSetting(OrderSetting orderSetting, HttpServletRequest request) {
        preInject(orderSetting, request);
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

    @Override
    public List<OrderSetting> listAvailableOrderSettingByStationId(Long stationId) {
        return orderSettingMapper.listAvailableOrderSettingByStationId(stationId);
    }

    @Override
    public boolean hasDefaultSetting(Long stationId) {
        int count = orderSettingMapper.countDefaultSetting(stationId);
        return count == 1 ? true : false;
    }

    @Override
    public OrderSetting getDefaultSetting(Long stationId) {
        return orderSettingMapper.getDefaultSetting(stationId);
    }
}
