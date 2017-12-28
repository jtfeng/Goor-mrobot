package cn.muye.erp.order.service;

import cn.mrobot.bean.erp.order.OperationOrder;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseCrudService;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
public interface OperationOrderService extends BaseCrudService<OperationOrder> {

    Long saveOrder(OperationOrder operationOrder);

    OperationOrder findOrderById(Long id);

    void updateHandleTimeAndState(OperationOrder operationOrder);

    List<OperationOrder> listAllOperationOrder(WhereRequest whereRequest);

    void updateReceiveTimeAndState(OperationOrder operationOrder);
}
