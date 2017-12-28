package cn.muye.erp.order.service.impl;

import cn.mrobot.bean.erp.order.OperationOrderApplianceXREF;
import cn.muye.erp.order.mapper.OperationOrderApplianceXREFMapper;
import cn.muye.erp.order.service.OperationOrderApplianceXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jelynn on 2017/12/12.
 */
@Service
public class OperationOrderApplianceXREFServiceImpl implements OperationOrderApplianceXREFService {

    @Autowired
    private OperationOrderApplianceXREFMapper operationOrderApplianceXREFMapper;

    @Override
    public void save(OperationOrderApplianceXREF operationOrderApplianceXREF) {
        operationOrderApplianceXREFMapper.save(operationOrderApplianceXREF);
    }

    @Override
    public List<OperationOrderApplianceXREF> findByOperationOrderId(Long operationOrderId) {
        return operationOrderApplianceXREFMapper.findByOperationOrderId(operationOrderId);
    }
}
