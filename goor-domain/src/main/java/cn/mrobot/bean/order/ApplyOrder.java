package cn.mrobot.bean.order;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by Selim on 2017/11/6.
 */
@Table(name = "OR_APPLY_ORDER")
public class ApplyOrder extends BaseBean {

    private Long applyStationId;    //发起申请站id

    private Long sendStationId;     //请求发送站id

    private Long orderId;            //关联的订单id

    private String applyNum;         //申请货物手输编号

    private Integer status;          //状态  0 申请中 1 已接收 2 被拒绝

    private Date dealDate;           //处理时间（接受或拒绝）

    @Transient
    private String applyStationName;  //申请站名

    public String getApplyStationName() {
        return applyStationName;
    }

    public void setApplyStationName(String applyStationName) {
        this.applyStationName = applyStationName;
    }

    public Long getApplyStationId() {
        return applyStationId;
    }

    public void setApplyStationId(Long applyStationId) {
        this.applyStationId = applyStationId;
    }

    public Long getSendStationId() {
        return sendStationId;
    }

    public void setSendStationId(Long sendStationId) {
        this.sendStationId = sendStationId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }
}
