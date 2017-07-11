package cn.muye.order.service;

import cn.mrobot.bean.order.OrderDetail;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderDetailService extends BaseService<OrderDetail> {
    List<OrderDetail> listOrderDetailByOrderId(Long orderId);
}
