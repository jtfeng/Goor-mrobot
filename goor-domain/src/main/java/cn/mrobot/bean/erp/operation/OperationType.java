package cn.mrobot.bean.erp.operation;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.DeleteBase;
import cn.mrobot.bean.erp.appliance.Appliance;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 手术类型
 *
 * @author Jelynn
 * @date 2017/12/19
 */
@Table(name = "ERP_OPERATION_TYPE")
public class OperationType extends DeleteBase{

    private String name;

    @Transient
    private OperationDepartmentType operationDepartmentType;

    private String searchName;

    @Transient
    private List<OperationDefaultApplianceXREF> applianceList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationDepartmentType getOperationDepartmentType() {
        return operationDepartmentType;
    }

    public void setOperationDepartmentType(OperationDepartmentType operationDepartmentType) {
        this.operationDepartmentType = operationDepartmentType;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public List<OperationDefaultApplianceXREF> getApplianceList() {
        return applianceList;
    }

    public void setApplianceList(List<OperationDefaultApplianceXREF> applianceList) {
        this.applianceList = applianceList;
    }

    public void init(){
        this.setCreateTime(new Date());
        this.setStoreId(100L);
        this.setDeleteFlag(Constant.NORMAL);
    }
}
