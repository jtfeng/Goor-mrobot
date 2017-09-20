package cn.muye.order.mapper;

import cn.mrobot.bean.order.OrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
public interface OrderSettingMapper{

    OrderSetting getById(@Param("id") Long id);

    int saveOrderSetting(OrderSetting orderSetting);

    int updateOrderSetting(OrderSetting orderSetting);

    List<OrderSetting> listAvailableOrderSettingByStationId(@Param("stationId")Long stationId);

    int countDefaultSetting(@Param("stationId")Long stationId);

    OrderSetting getDefaultSetting(@Param("stationId")Long stationId);
}
