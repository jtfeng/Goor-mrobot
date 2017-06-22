package cn.mrobot.bean.common;

import java.io.Serializable;
import java.util.Date;

public class BaseBean implements Serializable {
    private Long merchantStoreId;//关联门店ID
    private Long createdBy;//创建用户
    private Date created;//创建时间

    public Long getMerchantStoreId() {
        return merchantStoreId;
    }

    public void setMerchantStoreId(Long merchantStoreId) {
        this.merchantStoreId = merchantStoreId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
