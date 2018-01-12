package cn.muye.erp.bindmac.mapper;

import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/7
 */
public interface StationMacPasswordXREFMapper extends MyMapper<StationMacPasswordXREF>{

    int saveStationMacPasswordXREF(StationMacPasswordXREF stationMacPasswordXREF);

    void updateByMac(StationMacPasswordXREF stationMacPasswordXREF);

    StationMacPasswordXREF findByMac(@Param("mac") String mac);

    List<StationMacPasswordXREF> listByConditions(@Param("mac") String mac, @Param("stationId") Long stationId);

    List<StationMacPasswordXREF> listAll();

    List<StationMacPasswordXREF> findByType(int code);

    StationMacPasswordXREF findXREFById(@Param("id") Long id);
}
