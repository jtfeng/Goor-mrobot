package cn.mrobot.bean.erp.operation;

import cn.mrobot.bean.erp.appliance.Appliance;

import javax.persistence.Table;

/**
 * 手术类型默认器械
 * @author Jelynn
 * @date 2017/12/19
 */
@Table(name = "ERP_OPERATION_DEFAULT_APPLIANCE_XREF")
public class OperationDefaultApplianceXREF {

    private Long operationTypeId;

    private Appliance appliance;

    private int number;

    public Long getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
