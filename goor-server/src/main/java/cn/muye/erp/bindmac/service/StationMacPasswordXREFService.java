package cn.muye.erp.bindmac.service;

import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
public interface StationMacPasswordXREFService extends BaseService<StationMacPasswordXREF> {

    int saveStationMacPasswordXREF(StationMacPasswordXREF stationMacPasswordXREF);

    List<StationMacPasswordXREF> list(WhereRequest whereRequest);

    List<StationMacPasswordXREF> findByType(StationMacPasswordXREF.Type type);

    StationMacPasswordXREF findByMac(String mac);

    void updateByMac(StationMacPasswordXREF stationMacPasswordXREF);

    int deleteByType(StationMacPasswordXREF.Type type);
}
