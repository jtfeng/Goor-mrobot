package cn.mrobot.bean.order;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 * 订单具体的分配位置
 */
@Table(name = "OR_ORDER_DETAIL")
public class OrderDetail extends BaseBean{

    private Long orderId; //关联的order

    private Long stationId; // 配送到的站点

    private Integer status; //当前部分任务是否完成 0 未完成  1完成

    private Integer place; // 运送点类型 0起始点 1中间点 2末尾点

    private Date finishDate; // 配送到确认时间

    @Transient
    private String stationName;  //站名

    public OrderDetail() {
    }

    public OrderDetail(Long id) {
        super(id);
    }

    @Transient
    private List<GoodsInfo> goodsInfoList; //货物详情单

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public List<GoodsInfo> getGoodsInfoList() {
        return goodsInfoList;
    }

    public void setGoodsInfoList(List<GoodsInfo> goodsInfoList) {
        this.goodsInfoList = goodsInfoList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
