package cn.muye.area.fixpath.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamRequestBody;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.fixpath.service.FixPathService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.service.consumer.topic.BaseMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Jelynn on 2017/9/18.
 */
@Service
public class FixPathServiceImpl implements FixPathService {

    private final static Logger logger = LoggerFactory.getLogger(FixPathServiceImpl.class);
    private static final int PATH_TYPE_IC = 1; //工控上传路径id
    private static final String PATH = "path"; //RoadPath name前缀

    @Autowired
    private PointService pointService;

    @Autowired
    private RoadPathService roadPathService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private BaseMessageService baseMessageService;

    @Override
    public void saveFixpathQuery(String senderId, Date sendTime, String messageData) throws Exception {

        if (StringUtil.isNullOrEmpty(messageData))
            return;

        JSONObject jsonObject = JSON.parseObject(messageData);
        String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
        String paths = jsonObject.getString(TopicConstants.PATHS);
        List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);
        for (PathDTO pathDTO : pathDTOList) {
            MapPoint startPoint = findOrSaveMapPoint(sceneName, pathDTO, true);
            MapPoint endPoint = findOrSaveMapPoint(sceneName, pathDTO, false);
            //封装RoadPath对象，保存数据库
            RoadPath roadPath = new RoadPath();
            roadPath.setSceneName(sceneName);
            roadPath.setMapName(pathDTO.getStartMap());
            roadPath.setPathId(pathDTO.getId() + "");
            //添加roadpath查询，根据场景，地图，pathid进行查询，如果存在，则更新，不存在则添加
            RoadPath roadPathDB = roadPathService.findRoadPath(roadPath);
            //继续封装参数
            roadPath.setStartPoint(startPoint.getId());
            roadPath.setEndPoint(endPoint.getId());
            roadPath.setPathType(PATH_TYPE_IC);
            roadPath.setPathName(PATH + pathDTO.getId());
            roadPath.setCreateTime(new Date());
            roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);

            //根据数据库查询结果判断是更新还是新增
            if (null != roadPathDB) {
                roadPath.setId(roadPathDB.getId());
                roadPathService.update(roadPath);  //更新
            } else {
                roadPathService.save(roadPath);  //新增
            }
        }
    }

    @Override
    public AjaxResult sendFixpathQuery(Long sceneId) throws Exception {
        String mapSceneName = sceneService.getRelatedMapNameBySceneId(sceneId);
        CopyOnWriteArraySet<String> robotCodeList = CacheInfoManager.getSceneRobotListCache(mapSceneName);
        if (null == robotCodeList) {
            logger.info("场景 {} 无在线机器人", mapSceneName);
            return AjaxResult.failed("场景" + mapSceneName + "无在线机器人");
        }
        for (String robotCode : robotCodeList) {
            Boolean online = CacheInfoManager.getRobotOnlineCache(robotCode);
            if (null == online || !online)
                continue;

            SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.FIXPATH_QUERY);
            JSONObject dataObject = new JSONObject();
            dataObject.put(TopicConstants.SCENE_NAME, mapSceneName);
            slamRequestBody.setData(dataObject);

            return baseMessageService.sendRobotMessage(robotCode, TopicConstants.APP_PUB, JSON.toJSONString(slamRequestBody));
        }
        return AjaxResult.failed("请求失败");
    }

    /**
     * 查询或者保存点_路径点不重复
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    private MapPoint findOrSaveMapPoint(String sceneName, PathDTO pathDTO, boolean start) {
        String mapName = pathDTO.getStartMap();
        String pointName = start ? pathDTO.getStartId() : pathDTO.getEndId();
        List<MapPoint> pointList = pointService.findByName(pointName, sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (null != pointList && pointList.size() > 0) {
            return pointList.get(0);
        }
        //封装mapPoint对象，保存数据库
        MapPoint mapPoint = new MapPoint();
        mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapPoint.setSceneName(sceneName);
        mapPoint.setMapName(mapName);
        mapPoint.setCreateTime(new Date());
        mapPoint.setPointName(start ? pathDTO.getStartId() : pathDTO.getEndId());
        mapPoint.setX(start ? pathDTO.getStartX() : pathDTO.getEndX());
        mapPoint.setY(start ? pathDTO.getStartY() : pathDTO.getEndY());
        mapPoint.setTh(start ? pathDTO.getStartTh() : pathDTO.getEndTh());
        pointService.save(mapPoint);
        return mapPoint;
    }

    /**
     * 查询或者保存点_路径点不重复
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointNoDuplicate(String sceneName, PathDTO pathDTO, boolean start, PointService pointService) {
        String mapName = pathDTO.getStartMap();
        String pointName = start ? pathDTO.getStartId() : pathDTO.getEndId();
        List<MapPoint> pointList = pointService.findByName(pointName, sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (null != pointList && pointList.size() > 0) {
            return pointList.get(0);
        }
        //封装mapPoint对象，保存数据库
        MapPoint mapPoint = new MapPoint();
        mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapPoint.setSceneName(sceneName);
        mapPoint.setMapName(mapName);
        mapPoint.setCreateTime(new Date());
        mapPoint.setPointName(pointName);
        mapPoint.setPointAlias(pointName + "_" + PATH + pathDTO.getId() + ( start ? "start" : "end" ));
        mapPoint.setX(start ? pathDTO.getStartX() : pathDTO.getEndX());
        mapPoint.setY(start ? pathDTO.getStartY() : pathDTO.getEndY());
        mapPoint.setTh(start ? pathDTO.getStartTh() : pathDTO.getEndTh());
        pointService.save(mapPoint);
        return mapPoint;
    }

    /**
     * 查询或者保存点——点名根据路径取
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPath(String sceneName, PathDTO pathDTO, boolean start , PointService pointService) {
        String mapName = pathDTO.getStartMap();
        String pointName = PATH + pathDTO.getId() + ( start ? "start" : "end" );
        String pointNamePre = start ? pathDTO.getStartId() : pathDTO.getEndId();
        List<MapPoint> pointList = pointService.findByName(pointName, sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (null != pointList && pointList.size() > 0) {
            return pointList.get(0);
        }
        //封装mapPoint对象，保存数据库
        MapPoint mapPoint = new MapPoint();
        mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapPoint.setSceneName(sceneName);
        mapPoint.setMapName(mapName);
        mapPoint.setCreateTime(new Date());
        mapPoint.setPointName(pointNamePre + "_" + pointName);
        mapPoint.setPointAlias(pointName);
        mapPoint.setX(start ? pathDTO.getStartX() : pathDTO.getEndX());
        mapPoint.setY(start ? pathDTO.getStartY() : pathDTO.getEndY());
        mapPoint.setTh(start ? pathDTO.getStartTh() : pathDTO.getEndTh());
        pointService.save(mapPoint);
        return mapPoint;
    }
}
