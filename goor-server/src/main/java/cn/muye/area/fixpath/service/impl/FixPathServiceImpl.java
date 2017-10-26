package cn.muye.area.fixpath.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamRequestBody;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.fixpath.service.FixPathService;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.service.consumer.topic.BaseMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.javac.code.Attribute;
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
//    private static final String PATH = "path"; //RoadPath name前缀

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

        if (StringUtil.isNullOrEmpty(messageData)){
            return;
        }

        JSONObject jsonObject = JSON.parseObject(messageData);
        String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
        String paths = jsonObject.getString(TopicConstants.PATHS);
        List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);

        //调用统一存储工控固定路径方法
        roadPathService.saveOrUpdateRoadPathByPathDTOListNoDuplicatePoint(pathDTOList, sceneName);
    }

    @Override
    public AjaxResult sendFixpathQuery(Long sceneId, String robotCode) throws Exception {
        String mapSceneName = sceneService.getRelatedMapNameBySceneId(sceneId);
        Boolean online = CacheInfoManager.getRobotOnlineCache(robotCode);
        if (null == online || !online){
            return AjaxResult.failed("机器人"+robotCode+"不在线");
        }
        if (StringUtil.isNullOrEmpty(mapSceneName)){
            return  AjaxResult.failed("未获取到当前场景关联的地图场景名");
        }

        SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.FIXPATH_QUERY);
        JSONObject dataObject = new JSONObject();
        dataObject.put(TopicConstants.SCENE_NAME, mapSceneName);
        slamRequestBody.setData(dataObject);

        return baseMessageService.sendRobotMessage(robotCode, TopicConstants.APP_PUB, JSON.toJSONString(slamRequestBody));
    }

    /**
     * 查询或者保存点_路径交叉点不重复
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPathNoDuplicate(String sceneName, PathDTO pathDTO, boolean start, PointService pointService) {
        return findOrSaveMapPointByPath(sceneName, pathDTO, start, pointService, false);
    }

    /**
     * 查询或者保存点——点名根据路径取，用于路径点交叉重复存储点方式
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPathDuplicate(String sceneName, PathDTO pathDTO, boolean start , PointService pointService) {
        return findOrSaveMapPointByPath(sceneName, pathDTO, start, pointService, true);
    }

    /**
     * 查询或者保存点_路径交叉点重复或者不重复命名规则不同
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @param isDuplicate   根据路径交叉点是否需要重复建点，采用不同的命名规则
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPath(String sceneName,
                                                    PathDTO pathDTO,
                                                    boolean start,
                                                    PointService pointService,
                                                    boolean isDuplicate) {
        String pointNameResult = "";
        String pointAliasResult = "";
        String mapName = pathDTO.getStartMap();
        String pointName = "";
        //根据路径交叉点是否需要重复建点，采用不同的命名规则
        if(isDuplicate) {
            pointName = Constant.PATH + pathDTO.getId() + ( start ? "start" : "end" );
            String pointNamePre = start ? pathDTO.getStartId() : pathDTO.getEndId();
            pointNameResult = pointNamePre + "_" + pointName;
            pointAliasResult = pointName;
        }
        else {
            pointName = start ? pathDTO.getStartId() : pathDTO.getEndId();
            pointNameResult = pointName;
            pointAliasResult = pointName + "_" + Constant.PATH + pathDTO.getId() + ( start ? "start" : "end" );
        }


        //只查找未配置的云端类型点，其他用于特殊任务的复制点，不在查找之列
        List<MapPoint> pointList = pointService.findByNameCloudType(pointName, sceneName, mapName,
                SearchConstants.FAKE_MERCHANT_STORE_ID, MapPointType.UNDEFINED);
        if (null != pointList && pointList.size() > 0) {
            MapPoint mapPointDB = pointList.get(0);
            //对于旧的未定义类型点，别名如果没含路径点path关键字，则修改别名
            if(!mapPointDB.getPointAlias().contains(Constant.PATH)) {
                mapPointDB.setPointName(pointNameResult);
                mapPointDB.setPointAlias(pointAliasResult);
                pointService.update(mapPointDB);
            }
            return mapPointDB;
        }
        //封装mapPoint对象，保存数据库
        MapPoint mapPoint = new MapPoint();
        mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapPoint.setSceneName(sceneName);
        mapPoint.setMapName(mapName);
        mapPoint.setCreateTime(new Date());
        mapPoint.setPointName(pointNameResult);
        mapPoint.setPointAlias(pointAliasResult);
        mapPoint.setX(start ? pathDTO.getStartX() : pathDTO.getEndX());
        mapPoint.setY(start ? pathDTO.getStartY() : pathDTO.getEndY());
        mapPoint.setTh(start ? pathDTO.getStartTh() : pathDTO.getEndTh());
        pointService.save(mapPoint);
        return mapPoint;
    }

    /**
     * 查询或者保存MapInfo——名称根据路径的起点和终点取
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @return 导航目标点
     */
    public static MapInfo findOrSaveMapInfoByPath(String sceneName, PathDTO pathDTO, boolean start , MapInfoService infoService) {
        String mapName = start?pathDTO.getStartMap() : pathDTO.getEndMap();
        List<MapInfo> infoList = infoService.findByName(sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (null != infoList && infoList.size() > 0) {
            return infoList.get(0);
        }
        //封装MapInfo对象，保存数据库
        MapInfo mapInfo = new MapInfo();
        mapInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapInfo.setSceneName(sceneName);
        mapInfo.setMapName(mapName);
        mapInfo.setCreateTime(new Date());
        infoService.save(mapInfo);
        return mapInfo;
    }
}
