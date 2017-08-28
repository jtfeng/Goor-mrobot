package cn.muye.order.mapper;

import cn.mrobot.bean.order.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderMapper {

    Order getById(@Param("id") Long id);

    int saveOrder(Order order);

    int updateOrder(Order order);

    List<Order> listByDomain(Order order);
}
