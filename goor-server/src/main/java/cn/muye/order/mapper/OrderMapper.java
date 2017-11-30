package cn.muye.order.mapper;

import cn.mrobot.bean.order.Order;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderMapper {

    Order getById(@Param("id") Long id);

    int saveOrder(Order order);

    int updateOrder(Order order);

    List<Order> listByDomain(Order order);

    void returnToWaitOrder(@Param("id") Long orderId, @Param("status")Integer status);

    List<Order> listOrdersByStation(@Param("stationId")Long stationId);

    Order findFirstWaitOrder(List<Long> stationList);

    List<Long> listProcessingOrderIdsToday(@Param("beforeTime")Date beforeTime, @Param("currentTime")Date currentTime);

    List<Order> listDoneOrdersByRobotIdDateDesc(@Param("robotId")Long robotId);

    List<Order> listDoneOrdersByRobotIdListDateDesc(List<Long> robotIdList);
}
