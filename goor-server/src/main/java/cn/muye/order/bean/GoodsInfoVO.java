package cn.muye.order.bean;

import cn.mrobot.bean.order.Goods;

/**
 * Created by Selim on 2017/7/17.
 */
public class GoodsInfoVO {

    private Goods goods;

    private Integer num;

    private Integer boxNum;

    private String password;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
