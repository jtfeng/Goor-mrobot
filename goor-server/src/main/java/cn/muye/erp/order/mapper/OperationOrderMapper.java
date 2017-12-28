package cn.muye.erp.order.mapper;

import cn.mrobot.bean.erp.order.OperationOrder;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
public interface OperationOrderMapper extends MyMapper<OperationOrder> {

    Long saveOrder(OperationOrder operationOrder);

    OperationOrder findOrderById(@Param("id") Long id);

    void updateHandleTimeAndState(@Param("id") Long id, @Param("handleTime") Date handleTime, @Param("state") int state);

    void updateReceiveTimeAndState(@Param("id") Long id, @Param("receiveTime") Date receiveTime, @Param("state") int state);

    List<OperationOrder> listOperationOrderByConditions(@Param("stationId") Long stationId,
                                                        @Param("state") Integer state,
                                                        @Param("type") Integer type,
                                                        @Param("applianceId") Integer applianceId);

    List<OperationOrder> listAllOperationOrder();
}
