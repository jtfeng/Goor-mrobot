package cn.muye.order.service.impl;

import cn.mrobot.bean.order.GoodsInfo;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.GoodsInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class GoodsInfoServiceImpl extends BaseServiceImpl<GoodsInfo> implements GoodsInfoService {
}
