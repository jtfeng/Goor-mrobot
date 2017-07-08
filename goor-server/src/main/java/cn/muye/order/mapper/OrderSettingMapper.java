package cn.muye.order.mapper;

import cn.mrobot.bean.order.OrderSetting;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Selim on 2017/7/6.
 */
public interface OrderSettingMapper {

    OrderSetting getById(@Param("id") Long id);

    int saveOrderSetting(OrderSetting orderSetting);

    int updateOrderSetting(OrderSetting orderSetting);
}
