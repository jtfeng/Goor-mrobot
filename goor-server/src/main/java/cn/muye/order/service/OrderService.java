package cn.muye.order.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.order.Order;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderService {

    void saveWaitOrder(Order order);

    AjaxResult saveOrder(Order order);

    AjaxResult savePathOrder(Order order);

    Order getOrder(Long id);

    void backToStartPoint(String robotCode, MapPoint mapPoint);

    void checkWaitOrders();

    void changeOrderStatus(Long id, Integer status);

    List<Order> listOrdersByStationAndStatus(Long stationId, Integer orderStatus);

    List<Order> listOrdersByStation(Long stationId, Integer page, Integer pageSize);
}
