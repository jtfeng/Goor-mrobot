package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationMapPointXREF;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.service.StationMapPointXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/17
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
@Transactional
@Service
public class StationServiceImpl implements StationService {

	@Autowired
    protected StationMapper stationMapper;
	@Autowired
	protected StationMapPointXREFService stationMapPointXREFService;
	@Autowired
	protected PointService pointService;

	@Override
	public long save(Station station) {
		long num = stationMapper.insert(station);
		//关联点
		saveStationMapPointXREFByStation(station);
		return num;
	}

	@Override
	public void update(Station station) {
		//删除关联的点，然后重新添加关联的点
		stationMapPointXREFService.deleteByStationId(station.getId());

		stationMapper.updateByPrimaryKey(station);

		//重新关联点
		saveStationMapPointXREFByStation(station);

	}

	private void saveStationMapPointXREFByStation(Station station) {
		//重新关联点
		Long stationId = station.getId();
		List<MapPoint> mapPointList = station.getMapPoints();
		if(mapPointList != null && mapPointList.size() > 0) {
			for(MapPoint mapPoint : mapPointList) {
				StationMapPointXREF stationMapPointXREF = new StationMapPointXREF();
				stationMapPointXREF.setStationId(stationId);
				stationMapPointXREF.setMapPointId(mapPoint.getId());
				stationMapPointXREFService.save(stationMapPointXREF);
			}
		}
	}

	@Override
	public Station findById(long id) {
		return stationMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Station> list(WhereRequest whereRequest) {
		List<Station> temp = new ArrayList<Station>();
		List<Station> result = new ArrayList<Station>();
		if(whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
			//TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = stationMapper.list(name);*/

			//方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
			Example example = new Example(Station.class);
			example.createCriteria().andCondition("NAME like", "%" + name + "%");
			example.setOrderByClause("ID DESC");
			temp = stationMapper.selectByExample(example);
		}else {
			//TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = stationMapper.list(null);*/

			//方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
			temp = stationMapper.selectAll();
		}

		//如果用公共Mapper查询，则需手动用For循环把关联的点放到站里面
		if(temp != null && temp.size() > 0) {
			for(Station station:temp) {
				List<MapPoint> resultMapPoint = new ArrayList<MapPoint>();
				WhereRequest whereRequestTemp = new WhereRequest();
				whereRequest.setQueryObj("{\""+SearchConstants.SEARCH_STATION_ID+"\":"+station.getId()+"}");
				List<StationMapPointXREF> stationMapPointXREFList = stationMapPointXREFService.list(whereRequestTemp);
				//如果没有关联点，则直接返回
				if(stationMapPointXREFList == null || stationMapPointXREFList.size() <= 0) {
					result.add(station);
					continue;
				}

				//如果有关联点，则更新station的点列表
				for(StationMapPointXREF stationMapPointXREF : stationMapPointXREFList) {
					MapPoint mapPoint = pointService.findById(stationMapPointXREF.getMapPointId());
					resultMapPoint.add(mapPoint);
				}
				station.setMapPoints(resultMapPoint);
				result.add(station);
			}
		}

		return result;
	}

	@Override
	public List<Station> listByName(String name) {
		Example example = new Example(Station.class);
		example.createCriteria().andCondition("NAME =", name);
		return stationMapper.selectByExample(example);
	}

	@Override
	public void delete(Station station) {
		stationMapper.delete(station);
	}


}

