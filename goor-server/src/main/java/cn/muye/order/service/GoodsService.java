package cn.muye.order.service;

import cn.mrobot.bean.order.Goods;
import cn.muye.base.service.BaseService;

/**
 * Created by Selim on 2017/7/7.
 */
public interface GoodsService extends BaseService<Goods>{
    void fakeDelete(Long id);
}
