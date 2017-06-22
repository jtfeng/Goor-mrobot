package cn.mrobot.bean.common;

import java.io.Serializable;
import java.util.Date;

public class BaseBean implements Serializable {
    private Long merchantId;//关联门店ID
    private Long createdBy;//创建用户
    private Date created;//创建时间

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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
