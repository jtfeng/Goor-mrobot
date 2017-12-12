package cn.muye.erp.password.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.erp.password.OperationPadPasswordXREF;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.password.mapper.OperationPakPasswordXREFMapper;
import cn.muye.erp.password.service.OperationPadPasswordXREFService;
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
public class OperationPadPasswordXREFServiceImpl extends BaseServiceImpl<OperationPadPasswordXREF> implements OperationPadPasswordXREFService {

    @Autowired
    private OperationPakPasswordXREFMapper mapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public OperationPadPasswordXREF findByMacAndPassword(String mac, String password) {
        Example example = new Example(OperationPadPasswordXREF.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(mac)) {
            criteria.andCondition("MAC='" + mac + "'");
        }
        if (StringUtil.isNotBlank(password)) {
            criteria.andCondition("PASSWORD='" + password + "'");
        }
        List<OperationPadPasswordXREF> list = mapper.selectByExample(example);
        if (null != list && list.size() > 0) {
            OperationPadPasswordXREF padPasswordXREF = list.get(0);
            return addStation(padPasswordXREF);
        }
        return null;
    }

    @Override
    public List<OperationPadPasswordXREF> list(WhereRequest whereRequest) {
        List<OperationPadPasswordXREF> list = new ArrayList<>();
        if (StringUtil.isNotBlank(whereRequest.getQueryObj())) {
            JSONObject map = JSONObject.parseObject(whereRequest.getQueryObj());
            String mac = map.getString(SearchConstants.SEARCH_MAC);
            Long stationId = map.getLong(SearchConstants.SEARCH_STATION_ID);
            String password = map.getString(SearchConstants.SEARCH_PASSWORD);
            Example example = new Example(OperationPadPasswordXREF.class);
            Example.Criteria criteria = example.createCriteria();
            if (StringUtil.isNotBlank(mac)) {
                criteria.andCondition("MAC='" + mac + "'");
            }
            if (StringUtil.isNotBlank(password)) {
                criteria.andCondition("PASSWORD='" + password + "'");
            }
            if (null != stationId) {
                criteria.andCondition("STATION_ID=" + stationId + "");
            }
            example.setOrderByClause("CREATE_TIME desc");
            list = mapper.selectByExample(example);
        } else {
            list = mapper.selectAll();
        }
        return addStation(list);
    }

    @Override
    public OperationPadPasswordXREF findOne(OperationPadPasswordXREF operaXREF) {
        return mapper.selectOne(operaXREF);
    }

    @Override
    public List<OperationPadPasswordXREF> findByStationId(Long stationId) {
        Example example = new Example(OperationPadPasswordXREF.class);
        example.createCriteria().andCondition("STATION_ID=" + stationId + "");
        return mapper.selectByExample(example);
    }

    private List<OperationPadPasswordXREF> addStation(List<OperationPadPasswordXREF> list) {
        for (int i = 0; i < list.size(); i++) {
            OperationPadPasswordXREF operationPadPasswordXREF = list.get(i);
            addStation(operationPadPasswordXREF);
            list.set(i, operationPadPasswordXREF);
        }
        return list;
    }

    @Override
    public OperationPadPasswordXREF addStation(OperationPadPasswordXREF operationPadPasswordXREF) {
        Station station = stationMapper.selectByPrimaryKey(operationPadPasswordXREF.getStationId());
        operationPadPasswordXREF.setStation(station);
        return operationPadPasswordXREF;
    }
}
