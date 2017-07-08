package cn.muye.order.service.impl;

import cn.mrobot.bean.order.OrderDetail;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail> implements OrderDetailService {
}
