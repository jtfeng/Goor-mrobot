package cn.mrobot.bean.erp.operation;

import cn.mrobot.bean.erp.ApplianceXREF;
import cn.mrobot.bean.erp.appliance.Appliance;

import javax.persistence.Table;

/**
 * 手术类型默认器械
 * @author Jelynn
 * @date 2017/12/19
 */
@Table(name = "ERP_OPERATION_DEFAULT_APPLIANCE_XREF")
public class OperationDefaultApplianceXREF extends ApplianceXREF{

    private Long operationTypeId;

    public Long getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

}
