package cn.muye.order.service;

import cn.mrobot.bean.order.OrderSetting;

/**
 * Created by Selim on 2017/7/6.
 */
public interface OrderSettingService {
    OrderSetting getById(Long id);

    int saveOrderSetting(OrderSetting orderSetting);

    int updateOrderSetting(OrderSetting orderSetting);

    int deleteOrderSetting(Long id);
}
