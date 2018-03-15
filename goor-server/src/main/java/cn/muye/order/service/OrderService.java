package cn.muye.order.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.order.Order;

import java.util.Date;
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

    void checkWaitOrders() throws Exception;

    void robotRequestWaitOrder(String robotCode);

    void stopAllMissions(Long order);

    void checkOrderMissionOverTime();

    void changeOrderStatus(Long id, Integer status);

    //检测等待订单一小时前的
    void checkWaitOrdersOneHourAgo();

    List<Order> listOrdersByStationAndStatus(Long stationId, Integer orderStatus);

    List<Order> listOrdersByStation(Long stationId, Integer page, Integer pageSize);

    List<Order> listPageOrderLogsByRobotId(Long robotId, Integer page, Integer pageSize);

    List<Order> listPageOrderLogsByStationId(Long stationId, Integer page, Integer pageSize);

    List<Order> listOrdersByStationAndDate(Long stationId, Date startDate, Date endDate);
}
