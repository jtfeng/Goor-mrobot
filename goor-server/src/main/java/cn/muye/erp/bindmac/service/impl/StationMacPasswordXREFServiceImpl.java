package cn.muye.erp.bindmac.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.bindmac.mapper.StationMacPasswordXREFMapper;
import cn.muye.erp.bindmac.service.StationMacPasswordXREFService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
@Service
public class StationMacPasswordXREFServiceImpl extends BaseServiceImpl<StationMacPasswordXREF> implements StationMacPasswordXREFService {

    @Autowired
    private StationMacPasswordXREFMapper mapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public int saveStationMacPasswordXREF(StationMacPasswordXREF stationMacPasswordXREF) {
        return mapper.saveStationMacPasswordXREF(stationMacPasswordXREF);
    }

    @Override
    public List<StationMacPasswordXREF> list(WhereRequest whereRequest) {
        List<StationMacPasswordXREF> list = new ArrayList<>();
        if (StringUtil.isNotBlank(whereRequest.getQueryObj())) {
            JSONObject map = JSONObject.parseObject(whereRequest.getQueryObj());
            String mac = map.getString(SearchConstants.SEARCH_MAC);
            Long stationId = map.getLong(SearchConstants.SEARCH_STATION_ID);
            list = mapper.listByConditions(mac, stationId);
        } else {
            list = mapper.listAll();
        }
        return list;
    }

    @Override
    public List<StationMacPasswordXREF> findByType(StationMacPasswordXREF.Type type) {
        Example example = new Example(StationMacPasswordXREF.class);
        example.createCriteria().andCondition("TYPE=" + type.getCode() + "");
        example.setOrderByClause("CREATE_TIME desc");
        return mapper.selectByExample(example);
    }

    @Override
    public StationMacPasswordXREF findByMac(String mac) {
        return mapper.findByMac(mac);
    }

    @Override
    public void updateByMac(StationMacPasswordXREF stationMacPasswordXREF) {
        mapper.updateByMac(stationMacPasswordXREF);
    }

    @Override
    public int deleteByType(StationMacPasswordXREF.Type type) {
        Example example = new Example(StationMacPasswordXREF.class);
        example.createCriteria().andCondition("TYPE=" + type.getCode() + "");
        return mapper.deleteByExample(example);
    }
}
