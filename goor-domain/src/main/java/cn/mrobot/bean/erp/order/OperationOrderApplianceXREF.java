package cn.mrobot.bean.erp.order;

import cn.mrobot.bean.erp.ApplianceXREF;
import cn.mrobot.bean.erp.appliance.Appliance;

import javax.persistence.Table;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
@Table(name = "ERP_OPERATION_ORDER_APPLIANCE_XREF")
public class OperationOrderApplianceXREF extends ApplianceXREF{

    private Long operationOrderId;

    public Long getOperationOrderId() {
        return operationOrderId;
    }

    public void setOperationOrderId(Long operationOrderId) {
        this.operationOrderId = operationOrderId;
    }
}