package cn.muye.assets.goods.service.impl;

import cn.mrobot.bean.assets.good.GoodsType;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/10.
 */
@Service
@Transactional
public class GoodsTypeServiceImpl extends BaseServiceImpl<GoodsType> implements GoodsTypeService {
}
