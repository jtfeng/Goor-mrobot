package cn.muye.order.service;

import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface OrderDetailService extends BaseService<OrderDetail> {
    List<OrderDetail> listOrderDetailByOrderId(Long orderId);

    //到站完成任务时需调用此service
    void finishedDetailTask(Long id);

    //护士站分页显示
    List<OrderDetail> listStationTasks(Long stationId, WhereRequest whereRequest);

    //获取orderDetail 详细信息
    OrderDetail getOrderDetailInfo(Long id);
}
