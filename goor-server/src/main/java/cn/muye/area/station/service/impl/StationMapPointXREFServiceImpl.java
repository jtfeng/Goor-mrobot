package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.station.StationMapPointXREF;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.mapper.StationMapPointXREFMapper;
import cn.muye.area.station.service.StationMapPointXREFService;
import cn.muye.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/20
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
@Transactional
@Service
public class StationMapPointXREFServiceImpl implements StationMapPointXREFService {

	@Autowired
    protected StationMapPointXREFMapper mapper;

	@Override
	public long save(StationMapPointXREF stationMapPointXREF) {
		return mapper.insert(stationMapPointXREF);
	}

	@Override
	public void update(StationMapPointXREF stationMapPointXREF) {
		mapper.updateByPrimaryKey(stationMapPointXREF);
	}

	@Override
	public StationMapPointXREF get(long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public List<StationMapPointXREF> list(WhereRequest whereRequest) {
		if(whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object id = map.get(SearchConstants.SEARCH_STATION_ID);
			Example example = new Example(StationMapPointXREF.class);
			example.createCriteria().andCondition("STATION_ID =", id);
			example.setOrderByClause("MAP_POINT_ID ASC");
			return mapper.selectByExample(example);
		}else {
			return null;
		}
	}

	@Override
	public void delete(long id) {
		mapper.deleteByPrimaryKey(id);
	}

	@Override
	public void deleteByStationId(long id) {
		Example example = new Example(StationMapPointXREF.class);
		example.createCriteria().andCondition("STATION_ID =", id);
		mapper.deleteByExample(example);
	}


}

