package cn.muye.order.service.impl;

import cn.mrobot.bean.order.Goods;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.GoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/7.
 */
@Service
@Transactional
public class GoodsServiceImpl extends BaseServiceImpl<Goods> implements GoodsService{
    @Override
    public void fakeDelete(Long id) {
        Goods goods = new Goods(id);
        goods.setDeleteStatus(Boolean.TRUE);
        super.updateSelectiveByStoreId(goods);
    }
}
