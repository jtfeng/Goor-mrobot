package cn.muye.area.point.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.point.cascade.CascadeMapPoint;
import cn.mrobot.bean.area.point.cascade.CascadeMapPointType;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
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
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

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
@Transactional
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
    public int save(List<MapPoint> mapPointList) {
        return pointMapper.insertList(mapPointList);
    }

    @Override
    public void delete(MapPoint mapPoint) {
        pointMapper.delete(mapPoint);
    }

    @Override
    public void delete(String sceneName, String mapName, long storeId) {
        Condition condition = new Condition(MapPoint.class);
        condition.createCriteria().andCondition("MAP_NAME ='" + mapName + "'")
                .andCondition("SCENE_NAME ='" + sceneName + "'")
                .andCondition("STORE_ID ="+ storeId);
        pointMapper.deleteByExample(condition);
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
    public List<MapPoint> findByName(String pointName, String sceneName, String mapName, long storeId) {
        Condition condition = new Condition(MapPoint.class);
        condition.createCriteria().andCondition("POINT_NAME ='" + pointName + "'")
                .andCondition("SCENE_NAME = '" + sceneName + "'")
                .andCondition("MAP_NAME = '" + mapName + "'")
                .andCondition("STORE_ID ="+storeId);
        condition.setOrderByClause("POINT_NAME desc");
        return pointMapper.selectByExample(condition);
    }

    @Override
    public List<MapPoint> list(WhereRequest whereRequest, long storeId) {
        Condition condition = new Condition(MapPoint.class);
        Example.Criteria criteria = condition.createCriteria();
        if (whereRequest.getQueryObj() != null) {
            JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
            Object pointName = jsonObject.get(SearchConstants.SEARCH_POINT_NAME);
            Object pointAlias = jsonObject.get(SearchConstants.SEARCH_POINT_ALIAS);
            Object sceneName = jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
            Object mapName = jsonObject.get(SearchConstants.SEARCH_MAP_NAME);
            Object mapPointTypeId = jsonObject.get(SearchConstants.SEARCH_MAP_POINT_TYPE_ID);
            if (pointName != null) {
                criteria.andCondition("POINT_NAME like %" + pointName + "%");
            }
            if (pointAlias != null) {
                criteria.andCondition("POINT_ALIAS like %" + pointAlias + "%");
            }
            if (sceneName != null) {
                criteria.andCondition("SCENE_NAME like %" + sceneName + "%");
            }
            if (sceneName != null) {
                criteria.andCondition("MAP_NAME like %" + mapName + "%");
            }
            if (mapPointTypeId != null) {
                criteria.andCondition("MAP_POINT_TYPE_ID ="+mapPointTypeId);
            }
        }
        criteria.andCondition("STORE_ID = "+storeId);
        condition.setOrderByClause("SCENE_NAME, MAP_NAME ASC");

        List<MapPoint> mapPointList = pointMapper.selectByExample(condition);
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
        condition.createCriteria().andCondition("SCENE_NAME ='" + sceneName + "'");
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

    @Override
    public List<CascadePoint> cascadeMapPoint() {
        List<CascadePoint> cascadePointList = new ArrayList<>();
        //获取场景名
        List<String> sceneNameList = pointMapper.selectSceneName(SearchConstants.FAKE_MERCHANT_STORE_ID);
        for (int a = 0; a < sceneNameList.size(); a++) {
            CascadePoint cascadePoint = new CascadePoint();
            //根据场景名获取地图名
            String sceneName = sceneNameList.get(a);
            List<String> mapNameList = pointMapper.selectMapNameBySceneName(sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            List<CascadeMapPoint> cascadeMapPointList = new ArrayList<>();
            String mapName;
            int pointTypeId;
            for (int i = 0; i < mapNameList.size(); i++) {
                CascadeMapPoint cascadeMapPoint = new CascadeMapPoint();
                mapName = mapNameList.get(i);
                //根据场景名,地图名 获取点类型
                List<Integer> pointTypeIdList = pointMapper.selectPointTypeByMapName(sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
                List<CascadeMapPointType> cascadeMapPointTypeList = new ArrayList<>();

                for (int j = 0; j < pointTypeIdList.size(); j++) {
                    CascadeMapPointType cascadeMapPointType = new CascadeMapPointType();
                    pointTypeId = pointTypeIdList.get(j);
                    //根据场景名,地图名 ，点类型 获取点列表
                    List<MapPoint> mapPointList = pointMapper.selectPointByPointTypeMapName(sceneName, mapName, pointTypeId,SearchConstants.FAKE_MERCHANT_STORE_ID);
                    cascadeMapPointType.setValue(pointTypeId);
                    cascadeMapPointType.setLabel(MapPointType.getValue(pointTypeId));
                    cascadeMapPointType.setChildren(mapPointList);
                    cascadeMapPointTypeList.add(cascadeMapPointType);
                }

                cascadeMapPoint.setValue(i);
                cascadeMapPoint.setLabel(mapName);
                cascadeMapPoint.setChildren(cascadeMapPointTypeList);
                cascadeMapPointList.add(cascadeMapPoint);
            }
            cascadePoint.setValue(a);
            cascadePoint.setLabel(sceneName);
            cascadePoint.setChildren(cascadeMapPointList);
            cascadePointList.add(cascadePoint);
        }

        return cascadePointList;
    }
}
