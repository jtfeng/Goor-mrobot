package cn.mrobot.bean.erp.appliance;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.DeleteBase;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@Table(name = "ERP_APPLIANCE_PACKAGE_TYPE")
public class AppliancePackageType extends DeleteBase {

    private String name;

    public AppliancePackageType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        public AppliancePackageType build() {
            return new AppliancePackageType(this);
        }
    }

    public AppliancePackageType(Builder builder) {
        this.setId(builder.id);
        this.setDeleteFlag(builder.deleteFlag);
        this.setDeleteTime(builder.deleteTime);
        this.name = builder.name;
    }

    public void init() {
        this.setCreateTime(new Date());
        this.setStoreId(100L);
        this.setDeleteFlag(Constant.NORMAL);
    }
}
