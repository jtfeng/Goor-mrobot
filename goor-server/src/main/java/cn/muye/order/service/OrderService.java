package cn.muye.order.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.order.Order;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderService {

    void saveWaitOrder(Order order, HttpServletRequest request);

    AjaxResult saveOrder(Order order, HttpServletRequest request);

    AjaxResult savePathOrder(Order order, HttpServletRequest request);

    Order getOrder(Long id);

    void backToStartPoint(String robotCode, MapPoint mapPoint, HttpServletRequest request);

    void checkWaitOrders(HttpServletRequest request);
}
