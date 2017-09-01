package cn.muye.order.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.order.Order;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderService {

    void saveWaitOrder(Order order);

    AjaxResult saveOrder(Order order);

    AjaxResult savePathOrder(Order order);

    Order getOrder(Long id);

    void checkWaitOrders();

    void backToStartPoint(String robotCode, MapPoint mapPoint);
}
