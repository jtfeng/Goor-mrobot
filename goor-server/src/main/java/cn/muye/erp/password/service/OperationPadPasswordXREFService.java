package cn.muye.erp.password.service;

import cn.mrobot.bean.erp.password.OperationPadPasswordXREF;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
public interface OperationPadPasswordXREFService extends BaseService<OperationPadPasswordXREF> {

    OperationPadPasswordXREF findByMacAndPassword(String mac, String password);

    List<OperationPadPasswordXREF> list(WhereRequest whereRequest);

    OperationPadPasswordXREF findOne(OperationPadPasswordXREF operaXREF);

    List<OperationPadPasswordXREF> findByStationId(Long stationId);

    OperationPadPasswordXREF addStation(OperationPadPasswordXREF operationPadPasswordXREF);

}
