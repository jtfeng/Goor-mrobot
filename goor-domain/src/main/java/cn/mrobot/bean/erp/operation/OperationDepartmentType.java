package cn.mrobot.bean.erp.operation;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.DeleteBase;
import com.alibaba.fastjson.annotation.JSONField;

import javax.naming.directory.SearchControls;
import javax.persistence.Table;
import java.util.Date;

/**
 * 手术科室类别
 *
 * @author Jelynn
 * @date 2017/12/19
 */
@Table(name = "ERP_OPERATION_DEPARTMENT_TYPE")
public class OperationDepartmentType extends DeleteBase {

    private String name;

    public OperationDepartmentType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init() {
        this.setDeleteFlag(Constant.NORMAL);
        this.setCreateTime(new Date());
        this.setStoreId(100L);
    }

    public static class Builder {
        private String name;
        private int deleteFlag;
        private Date deleteTime;
        private Long id;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder deleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
            return this;
        }

        public Builder deleteTime(Date deleteTime) {
            this.deleteTime = deleteTime;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public OperationDepartmentType build() {
            return new OperationDepartmentType(this);
        }
    }

    public OperationDepartmentType(Builder builder) {
        this.setId(builder.id);
        this.setDeleteFlag(builder.deleteFlag);
        this.setDeleteTime(builder.deleteTime);
        this.name = builder.name;
    }
}
