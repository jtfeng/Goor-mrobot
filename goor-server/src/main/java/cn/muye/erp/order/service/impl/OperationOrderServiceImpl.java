package cn.muye.erp.order.service.impl;

import cn.mrobot.bean.erp.order.OperationOrder;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseCrudServiceImpl;
import cn.muye.erp.order.mapper.OperationOrderMapper;
import cn.muye.erp.order.service.OperationOrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
@Service
public class OperationOrderServiceImpl extends BaseCrudServiceImpl<OperationOrder> implements OperationOrderService {

    @Autowired
    private OperationOrderMapper operationOrderMapper;

    @Override
    public Long saveOrder(OperationOrder operationOrder) {
        return operationOrderMapper.saveOrder(operationOrder);
    }

    @Override
    public OperationOrder findOrderById(Long id) {
        return operationOrderMapper.findOrderById(id);
    }

    @Override
    public void updateHandleTimeAndState(OperationOrder operationOrder) {
        operationOrderMapper.updateHandleTimeAndState(operationOrder.getId(), operationOrder.getHandleTime(), operationOrder.getState());
    }

    @Override
    public List<OperationOrder> listAllOperationOrder(WhereRequest whereRequest) {
        String obj = whereRequest.getQueryObj();
        if (StringUtil.isNotBlank(obj)) {
            JSONObject map = JSONObject.parseObject(obj);
            Long stationId = map.getLong(SearchConstants.SEARCH_STATION_ID);
            Integer state = map.getInteger(SearchConstants.SEARCH_STATE);
            Integer type = map.getInteger(SearchConstants.SEARCH_TYPE);
            Integer applianceId = map.getInteger(SearchConstants.SEARCH_APPLIANCE_ID);
            return operationOrderMapper.listOperationOrderByConditions(stationId, state, type, applianceId);
        } else {
            return operationOrderMapper.listAllOperationOrder();
        }
    }

    @Override
    public void updateReceiveTimeAndState(OperationOrder operationOrder) {
        operationOrderMapper.updateReceiveTimeAndState(operationOrder.getId(), operationOrder.getReceiveTime(), operationOrder.getState());
    }

    @Override
    public List<OperationOrder> getOperationOrderByType(OperationOrder.Type type) {
        WhereRequest whereRequest = new WhereRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SearchConstants.SEARCH_TYPE, type.getCode());
        whereRequest.setQueryObj(JSON.toJSONString(jsonObject));
        return listAllOperationOrder(whereRequest);
    }

}
