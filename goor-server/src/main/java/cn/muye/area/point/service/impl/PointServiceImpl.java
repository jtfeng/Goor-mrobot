package cn.muye.area.point.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.mapper.PointMapper;
import cn.muye.area.point.service.PointService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/15
 * Time: 16:15
 * Describe:
 * Version:1.0
 */
@Service
public class PointServiceImpl implements PointService {

	private static Logger LOGGER = LoggerFactory.getLogger(PointServiceImpl.class);
	@Autowired
	private PointMapper pointMapper;

	@Override
	public long save(MapPoint mapPoint) {
		return pointMapper.insert(mapPoint);
	}

	@Override
	public void delete(MapPoint mapPoint) {
		pointMapper.delete(mapPoint);
	}

	@Override
	public void update(MapPoint mapPoint) {
		pointMapper.updateByPrimaryKey(mapPoint);
	}

	@Override
	public MapPoint findById(long id) {
		return pointMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<MapPoint> findByName(String pointName, String sceneName) {
		Condition condition = new Condition(MapPoint.class);
		condition.createCriteria().andCondition("POINT_NAME = '" + pointName + "'");
		condition.createCriteria().andCondition("SCENE_NAME = '" + sceneName + "'");
		condition.setOrderByClause("POINT_NAME desc");
		return pointMapper.selectByExample(condition);
	}

	@Override
	public List<MapPoint> list(WhereRequest whereRequest) {
		List<MapPoint> mapPointList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
			Object pointName = jsonObject.get(SearchConstants.SEARCH_POINT_NAME);
			Object pointAlias = jsonObject.get(SearchConstants.SEARCH_POINT_ALIAS);
			Object sceneName = jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
			Object mapPointTypeId = jsonObject.get(SearchConstants.SEARCH_MAP_POINT_TYPE_ID);
			Condition condition = new Condition(MapPoint.class);
			if (pointName != null) {
				condition.createCriteria().andCondition("POINT_NAME like '%" + pointName + "%'");
			}
			if (pointAlias != null) {
				condition.createCriteria().andCondition("POINT_ALIAS like '%" + pointName + "%'");
			}
			if (sceneName != null) {
				condition.createCriteria().andCondition("SCENE_NAME like '%" + pointName + "%'");
			}
			if (mapPointTypeId != null) {
				condition.createCriteria().andCondition("MAP_POINT_TYPE_ID =" + pointName);
			}

			mapPointList = pointMapper.selectByExample(condition);
		} else {
			mapPointList = pointMapper.selectAll();
		}
		//处理枚举类型
		List<MapPoint> result = new ArrayList<>();
		MapPoint mapPoint;
		for (int i = 0; i < mapPointList.size(); i++) {
			mapPoint = mapPointList.get(i);
			mapPoint.setMapPointType(MapPointType.getTypeJson(mapPoint.getMapPointTypeId()));
			result.add(mapPoint);
		}
		return result;
	}

	@Override
	public List<MapPoint> findBySceneName(String sceneName) {
		Condition condition = new Condition(MapPoint.class);
		condition.createCriteria().andCondition("SCENE_NAME = '" + sceneName + "'");
		condition.setOrderByClause("SCENE_NAME desc");
		return pointMapper.selectByExample(condition);
	}

	@Override
	public void handle(SlamResponseBody slamResponseBody) {

		if (TopicConstants.POINT_LOAD.equals(slamResponseBody.getSubName())) {
			//获取地图导航点
			JSONObject jsonObject = JSON.parseObject(slamResponseBody.getData().toString());
			String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
			String mapName = jsonObject.getString(TopicConstants.MAP_NAME);
			String points = jsonObject.getString(TopicConstants.POINTS);
			List<MapPoint> mapPointList = JSONArray.parseArray(points, MapPoint.class);
			MapPoint mapPoint;

			List<MapPoint> mapPointListDB = findBySceneName(sceneName);
			for (int i = 0; i < mapPointList.size(); i++) {
				mapPoint = mapPointList.get(i);
				mapPoint.setMapName(mapName);
				mapPoint.setSceneName(sceneName);
				//校验目标点名称的唯一性
				if (!mapPointListDB.contains(mapPoint)) {
					save(mapPoint);
				} else {
					LOGGER.info("已经存在导航点，sceneName= " + sceneName + ", PointName =" + mapPoint.getPointName());
				}
			}
		}
	}

}
