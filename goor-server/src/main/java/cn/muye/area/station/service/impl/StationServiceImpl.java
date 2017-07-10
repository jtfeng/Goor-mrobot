package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationMapPointXREF;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationMapPointXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
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
public class StationServiceImpl extends BaseServiceImpl<Station> implements StationService {

	@Autowired
	protected StationMapPointXREFService stationMapPointXREFService;
	@Autowired
	protected PointService pointService;

	@Override
	public int save(Station station) {
		int num = myMapper.insert(station);
		//关联点
		saveStationMapPointXREFByStation(station);
		return num;
	}

	@Override
	public int update(Station station) {
		//删除关联的点，然后重新添加关联的点
		stationMapPointXREFService.deleteByStationId(station.getId());

		int num = myMapper.updateByPrimaryKey(station);

		//重新关联点
		saveStationMapPointXREFByStation(station);

		return num;
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
	public Station findById(long id, long storeId) {
		Example example = new Example(Station.class);
		example.createCriteria().andCondition("ID =", id)
				.andCondition("STORE_ID =", storeId);
		example.setOrderByClause("ID DESC");
		List<Station> temp = myMapper.selectByExample(example);
		if(temp == null || temp.size() <= 0) {
			return null;
		}
		Station station = temp.get(0);
		List<MapPoint> resultMapPoint = new ArrayList<MapPoint>();
		List<StationMapPointXREF> stationMapPointXREFList = stationMapPointXREFService.listByStationId(id);
		//如果没有关联点，则直接返回
		if(stationMapPointXREFList != null && stationMapPointXREFList.size() > 0) {
			//如果有关联点，则更新station的点列表
			for(StationMapPointXREF stationMapPointXREF : stationMapPointXREFList) {
				MapPoint mapPoint = pointService.findById(stationMapPointXREF.getMapPointId());
				resultMapPoint.add(mapPoint);
			}
			station.setMapPoints(resultMapPoint);
		}

		return station;
	}

	@Override
	public List<Station> list(WhereRequest whereRequest, Long storeId) {
		//如果whereRequest不为null，则分页
		if(whereRequest != null) {
			PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
		}

		List<Station> stationList = new ArrayList<Station>();
		if(whereRequest != null && whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
			//TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = myMapper.list(name);*/

			//方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
			Example example = new Example(Station.class);
			example.createCriteria().andCondition("NAME like", "%" + name + "%")
					.andCondition("STORE_ID =", storeId);
			example.setOrderByClause("ID DESC");
			stationList = myMapper.selectByExample(example);
		}else {
			//TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = myMapper.list(null);*/

			//方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
			Example example = new Example(Station.class);
			example.setOrderByClause("ID DESC");
			//超级管理员传storeId=null，能查看所有站；医院管理员传storeId!=null，只能查看该医院的站
			if(storeId != null) {
				example.createCriteria().andCondition("STORE_ID =", storeId);
			}
			stationList = myMapper.selectByExample(example);
		}

		//如果用公共Mapper查询，则需手动用For循环把关联的点放到站里面
		if(stationList != null && stationList.size() > 0) {
			for(Station station:stationList) {
				List<MapPoint> resultMapPoint = new ArrayList<MapPoint>();
				List<StationMapPointXREF> stationMapPointXREFList = stationMapPointXREFService.listByStationId(station.getId());
				//如果没有关联点，则直接返回
				if(stationMapPointXREFList == null || stationMapPointXREFList.size() <= 0) {
					continue;
				}

				//如果有关联点，则更新station的点列表
				for(StationMapPointXREF stationMapPointXREF : stationMapPointXREFList) {
					MapPoint mapPoint = pointService.findById(stationMapPointXREF.getMapPointId());
					if(mapPoint == null) {
						//如果关联的点不存在，手动删除点的关联关系
						stationMapPointXREFService.deleteByPointId(stationMapPointXREF.getMapPointId());
						continue;
					}
					resultMapPoint.add(mapPoint);
				}

				if( resultMapPoint != null && resultMapPoint.size() > 0 ) {
					station.setMapPoints(resultMapPoint);
				}

			}
		}

		return stationList;
	}

	@Override
	public List<Station> listByName(String name) {
		Example example = new Example(Station.class);
		example.createCriteria().andCondition("NAME =", name);
		return myMapper.selectByExample(example);
	}

	@Override
	public int delete(Station station) {
		//先删除关联的点
		stationMapPointXREFService.deleteByStationId(station.getId());
		//再删除站
		int num = myMapper.delete(station);
		return num;
	}


}

