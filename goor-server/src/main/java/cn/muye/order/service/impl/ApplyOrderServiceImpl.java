package cn.muye.order.service.impl;

import cn.mrobot.bean.order.ApplyOrder;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.ApplyOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/11/9.
 */
@Service
@Transactional
public class ApplyOrderServiceImpl extends BaseServiceImpl<ApplyOrder> implements ApplyOrderService{
}
