package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.order.GoodsInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataUnload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sign_in_mode;  //0 表示不签收 1表示签收

    List<GoodsInfo> goodsInfos;

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public String getSign_in_mode() {
        return sign_in_mode;
    }

    public void setSign_in_mode(String sign_in_mode) {
        this.sign_in_mode = sign_in_mode;
    }
}
