package cn.muye.order.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.order.Order;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderService {
    AjaxResult saveOrder(Order order);

    Order getOrder(Long id);
}
