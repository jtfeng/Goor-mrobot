package cn.muye.order.service.impl;

import cn.mrobot.bean.order.GoodsInfo;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.service.GoodsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class GoodsInfoServiceImpl implements GoodsInfoService {

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Override
    public List<GoodsInfo> listGoodsInfoByDetailId(Long orderDetailId) {
        return goodsInfoMapper.listGoodsInfoByDetailId(orderDetailId);
    }
}
