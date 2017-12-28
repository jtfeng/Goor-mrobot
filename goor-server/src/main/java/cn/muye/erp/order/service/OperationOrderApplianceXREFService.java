package cn.muye.erp.order.service;

import cn.mrobot.bean.erp.order.OperationOrderApplianceXREF;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
public interface OperationOrderApplianceXREFService {

    void save(OperationOrderApplianceXREF operationOrderApplianceXREF);

    List<OperationOrderApplianceXREF> findByOperationOrderId(@Param("operationOrderId") Long operationOrderId);
}
