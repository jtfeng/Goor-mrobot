package cn.mrobot.bean.common;

import java.io.Serializable;
import java.util.Date;

public class BaseBean implements Serializable {
    private long merchantId;//关联门店ID
    private long createdBy;//创建用户
    private Date created;//创建时间

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
