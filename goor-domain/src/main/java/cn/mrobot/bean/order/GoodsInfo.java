package cn.mrobot.bean.order;

import javax.persistence.*;

/**
 * Created by Selim on 2017/7/6.
 */
@Table(name = "OR_GOODS_INFO")
public class GoodsInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderDetailId; //关联下单的id

    private Long goodsId; //关联物品id

    private Integer num; //数量

    private Integer boxNum; // 存放箱位 （若有货架 默认为null）

    @Transient
    private Goods goods; //对应物品

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
