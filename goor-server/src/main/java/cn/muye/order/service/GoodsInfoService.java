package cn.muye.order.service;

import cn.mrobot.bean.order.GoodsInfo;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface GoodsInfoService {
    List<GoodsInfo> listGoodsInfoByDetailId(Long orderDetailId);
}
