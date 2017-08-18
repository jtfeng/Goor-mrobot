package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.order.GoodsInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataUnload implements Serializable {

    private static final long serialVersionUID = 1L;

    List<GoodsInfo> goodsInfos;

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }
}
