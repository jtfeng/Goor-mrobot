package cn.mrobot.bean.order;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Selim on 2017/7/6.
 */
@Table(name = "OR_GOODS_INFO")
public class GoodsInfo extends BaseBean{

    private Long orderDetailId; //关联下单的id

    private Long goodsId; //关联物品id

    private Integer num; //数量

    @Transient
    private Goods goods; //对应物品

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
