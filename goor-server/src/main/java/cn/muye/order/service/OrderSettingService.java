package cn.muye.order.service;

import cn.mrobot.bean.order.OrderSetting;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
public interface OrderSettingService {
    OrderSetting getById(Long id);

    int saveOrderSetting(OrderSetting orderSetting, HttpServletRequest request);

    int updateOrderSetting(OrderSetting orderSetting);

    int deleteOrderSetting(Long id);

    List<OrderSetting> listAvailableOrderSettingByStationId(Long stationId);

    boolean hasDefaultSetting(Long stationId);

    OrderSetting getDefaultSetting(Long stationId);
}
