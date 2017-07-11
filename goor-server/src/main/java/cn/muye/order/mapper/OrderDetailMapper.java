package cn.muye.order.mapper;

import cn.mrobot.bean.order.OrderDetail;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderDetailMapper extends MyMapper<OrderDetail> {
    List<OrderDetail> listOrderDetailByOrderId(@Param("orderId")Long orderId);
}
