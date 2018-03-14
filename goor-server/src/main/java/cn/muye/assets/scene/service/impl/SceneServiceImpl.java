package cn.muye.assets.scene.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotChargerMapPointXREF;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapInfoMapper;
import cn.muye.area.map.mapper.MapZipMapper;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.mapper.StationRobotXREFMapper;
import cn.muye.area.station.mapper.StationStationXREFMapper;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotChargerMapPointXREFService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.service.missiontask.MissionFuncsService;
import cn.muye.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2017/7/3.
 */

/**
 * 实现类具体完成场景数据信息操作
 */
@Service
@Transactional
public class SceneServiceImpl extends BaseServiceImpl<Scene> implements SceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);
    private static final Long STORE_ID = 100L;
    @Autowired
    private MapSyncService mapSyncService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private SceneMapper sceneMapper;
    @Autowired
    private MapZipMapper mapZipMapper;
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private StationService stationService;
    @Autowired
    private MapInfoMapper mapInfoMapper;
    @Autowired
    private StationRobotXREFMapper stationRobotXREFMapper;
    @Autowired
    private StationStationXREFMapper stationStationXREFMapper;
    @Autowired
    private RobotMapZipXREFService robotMapZipXREFService;
    @Autowired
    private MessageSendHandleService messageSendHandleService;
    @Autowired
    private RobotChargerMapPointXREFService robotChargerMapPointXREFService;
    @Autowired
    private MissionFuncsService missionFuncsService;
    @Autowired
    private PointService pointService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    //保存添加场景与机器人之间的关系时候，需要加锁，以免事务未提交读取到脏数据
    private ReentrantLock lock = new ReentrantLock();

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @Override
    public List<Scene> list() throws Exception {
        return sceneMapper.selectAll();
    }

//    "scene”:[{
//    id:’',
//    name:’',
//    map:[{“id”:’’, name:’’}, … ],
//    station:[{id:’’, name:''}, ...]
//}
//	    ,...]

    @Override
    public Map getRobotStartAssets(String robotCode) throws Exception {
        // 存放查询结果
        Map<String, Object> baseData = new HashMap<String, Object>(2);
        Robot robot = robotService.getByCode(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (robot == null) {
            //表明当前请求的机器人未在云端注册，反馈对应错误信息，抛出错误信息，设置 error_code
            throw new Exception(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_JQRWZC"));
        }
        // 存放全部场景的详细信息
        List<Map<String, Object>> sceneList = Lists.newArrayList();
        // 获取全部场景信息
        List<Scene> dbScenes = sceneMapper.selectAll();
        List<Scene> scenes = Lists.newArrayList();
        dbScenes.forEach( (scene -> {
            if (scene.getActive() == 1) {
                // 激活状态的场景信息
                scenes.add(scene);
            }
        }));
        for (Scene scene : scenes) {
            // 遍历每一个场景，查询归属的子内容，定义一个容器存放对应的信息
            Map<String, Object> eachScene = new HashMap<String, Object>(5);
            // 存放 ID 信息
            eachScene.put("id", scene.getId());
            // 存放工控场景名称
            eachScene.put("name", sceneMapper.getRelatedMapNameBySceneId(scene.getId()));
            // 存放云端场景别名信息
            eachScene.put("alias", scene.getName());
            // 存放所属场景下站的信息 - Station - 站
            Example stationExample = new Example(Station.class);
            // 创建查询条件
            stationExample.createCriteria().andCondition("SCENE_ID =", scene.getId());
            // 站的 JSON 数组
            JSONArray stationJSONArray = new JSONArray();
                // 全部的出发点
            List<Long> originStationIds = Lists.newArrayList();
            log.info("全部的出发点为：" + originStationIds);
            stationStationXREFMapper.selectAll().forEach(stationStationXREF -> originStationIds.add(stationStationXREF.getOriginStationId()));
            stationMapper.selectByExample(stationExample).forEach(ele -> {
                log.info("当前遍历的站 ID 编号为：" + ele.getId());
                if (originStationIds.indexOf(ele.getId()) == -1) {
                    // 不是出发点，则不提供选择
                    return;
                }
                // 遍历每一个站信息进行内容转换
                JSONObject jsonObject = new JSONObject() {{ put("id", ele.getId()); put("name", ele.getName()); }};
                stationJSONArray.add(jsonObject);
            });
            eachScene.put("station", stationJSONArray);
            // 存放所属场景下的地图信息 - Map - 地图
            Example mapExample = new Example(MapInfo.class);
            mapExample.createCriteria().andCondition("SCENE_NAME =", String.valueOf(eachScene.get("name")));
            // 存放地图信息的 JSON 数组
            JSONArray mapJSONArray = new JSONArray();
            mapInfoMapper.selectByExample(mapExample).forEach(ele -> {
                JSONObject jsonObject = new JSONObject() {{
                    // 遍历每一个地图信息进行内容转换
                    put("id", ele.getId());put("name", ele.getMapName());put("alias", ele.getMapAlias()); }};
                mapJSONArray.add(jsonObject);
            });
            eachScene.put("map", mapJSONArray);
            // 存放所属场景下的充电桩信息
            JSONArray mapPointListDbJSONArray = new JSONArray();
            sceneMapper.findMapPointBySceneId(scene.getId(), SearchConstants.FAKE_MERCHANT_STORE_ID, MapPointType.CHARGER.getCaption())
                    .forEach(eachP -> {
                        JSONObject j = new JSONObject();
                        j.put("id", eachP.getId());j.put("name", eachP.getPointAlias());
                        mapPointListDbJSONArray.add(j);
                    });
            eachScene.put("chargePoint", mapPointListDbJSONArray);
            //
            sceneList.add(eachScene);
        }
        baseData.put("scene", sceneList);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // 存放当前指定机器人所属的资源关系
        JSONObject robotAssets = new JSONObject();
        // 当前机器人所绑定的 站 信息
        JSONArray robotJSONArray = new JSONArray();
        stationService.findStationsByRobotCode(robotCode).forEach(es -> {
            JSONObject j = new JSONObject();
            j.put("id", es.getId()); j.put("name", es.getName());
            robotJSONArray.add(j);
        });
        robotAssets.put("station", robotJSONArray);
        // 当前机器人所绑定的 充电桩 信息
        JSONArray robotMapPointListDbJSONArray = new JSONArray();
        List<MapPoint> chargerMapPointList = robotService.getChargerMapPointByRobotCode(robotCode,
                SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (chargerMapPointList != null && chargerMapPointList.size() > 0) {
            chargerMapPointList.forEach(eachP -> {
                JSONObject j = new JSONObject();
                // 别名名称用作客户端显示
                j.put("id", eachP.getId());
                j.put("name", eachP.getPointAlias());
                robotMapPointListDbJSONArray.add(j);
            });
        }
        robotAssets.put("chargePoint", robotMapPointListDbJSONArray);
        List<Scene> list = sceneMapper.findSceneByRobotCode(robotCode);
        JSONObject currentSceneObject = new JSONObject();
        if (list != null && list.size() != 0) {
            Scene currentScene = list.get(0);
            currentSceneObject.put("id", currentScene.getId());
            currentSceneObject.put("name", sceneMapper.getRelatedMapNameBySceneId(currentScene.getId()));
            currentSceneObject.put("alias", currentScene.getName());
        } else {
            currentSceneObject.put("id", "");currentSceneObject.put("name", "");currentSceneObject.put("alias", "");
        }
        // 当前机器人所绑定的场景信息
        robotAssets.put("scene", currentSceneObject);
        baseData.put("robotAssets", robotAssets);
        return baseData;
    }

    /**
     * 更新机器人与资源的绑定关系，更新最新关系
     * @param latestRobotAssets 最新反馈的资源绑定关系
     */
    @Override
    public void updateGetRobotStartAssets(String robotCode, JSONObject latestRobotAssets) throws Exception {
        // 传递的指定机器人的机器人编号
        Robot currentRobot = robotService.getByCode(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (currentRobot == null) {return;}
        // 当前指定机器人的 ID 编号信息
        Long robotId = currentRobot.getId();
        // 新选择的场景 ID 编号（sceneId、stationIds、chargerMapPointIds）
        Long sceneId = latestRobotAssets.getLong("sceneId");
        // 新选择的站 ID 编号数组信息
        JSONArray stationIds = latestRobotAssets.getJSONArray("stationIds");
        List<Long> stationIdList = new ArrayList<Long>();
        // 新选择的充电桩 ID 编号数组信息
        JSONArray chargerMapPointIds = latestRobotAssets.getJSONArray("chargerMapPointIds");
        // + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
        // + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
        // 重新更新机器人与场景之间的绑定关系
        sceneMapper.deleteRobotAndSceneRelationsByRobotCode(robotCode);
        sceneMapper.insertSceneAndRobotRelations(sceneId, Lists.newArrayList(robotId));
        // 重新更新机器人与站之间的绑定关系 (站绑定关系)
        stationMapper.deleteStationWithRobotRelationByRobotCode(robotCode);
        for (int i = 0 ; i < stationIds.size() ; i++) {
            // 遍历每一个站
            Long stationId = stationIds.getLong(i);
            if(stationId != null) {
                stationIdList.add(stationId);
            }
            // 添加新的机器人与站点之间的绑定关系
            StationRobotXREF stationRobotXREF = new StationRobotXREF();
            stationRobotXREF.setRobotId(robotService.getByCode(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID).getId());
            stationRobotXREF.setStationId(stationId);
            stationRobotXREFMapper.insert(stationRobotXREF);
        }
        // 重新更新机器人与充电桩之间的绑定关系
        robotChargerMapPointXREFService.deleteByRobotId(robotId);
        for (int i = 0 ; i < chargerMapPointIds.size() ; i++) {
            // 遍历每一个充电桩
            Long chargerMapPointId = chargerMapPointIds.getLong(i);
            // 删除初始机器人与充电桩点的绑定关系
            // 添加新的机器人与充电桩点之间的绑定关系
            RobotChargerMapPointXREF robotChargerMapPointXREF = new RobotChargerMapPointXREF();
            robotChargerMapPointXREF.setRobotId(robotId);
            robotChargerMapPointXREF.setChargerMapPointId(chargerMapPointId);
            robotChargerMapPointXREFService.save(robotChargerMapPointXREF);
        }

        //TODO 第一阶段，先做机器人执行完开机管理后，自动去充电点，但不执行充电任务，
        // 如果机器人没有绑定充电点，则执行去站的装货点。如果都没有，就原地待命。
//        missionFuncsService.sendRobotToStandByPoint(currentRobot, stationIdList, sceneId);
    }

    @Override
    public void replyGetRobotStartAssets(String uuid, String robotCode) {
        LogInfoUtils.info(robotCode, ModuleEnums.BOOT, LogType.BOOT_GET_ASSETS, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_JQRKJHQYDZYKS"));
        Map assetData = Maps.newHashMap();
        try {
            assetData = this.getRobotStartAssets(robotCode);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > ");
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > ");
        log.info(JSONObject.toJSONString(assetData));
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > ");
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > ");
        // 传回云端资源给开机管理请求的机器人    ||
        final Map assetFinalData = assetData;
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_PUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, String>() {{
            put("pub_name", TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_QUERY);
            put("uuid", uuid);
            put("data", JSONObject.toJSONString(assetFinalData));
            if (assetFinalData.size() == 0) {
                // 未注册，未查询到任何数据
                put("error_code", "1000");
            }else {
                put("error_code", "0");
            }
        }}))));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setReceiverId(robotCode);
        messageInfo.setSenderId("goor-server");
        messageInfo.setMessageType(MessageType.ROBOT_INFO);
        messageInfo.setMessageText(JSON.toJSONString(commonInfo));
        try {
            messageSendHandleService.sendCommandMessage(true, false, robotCode, messageInfo);
            LogInfoUtils.info(robotCode, ModuleEnums.BOOT, LogType.BOOT_GET_ASSETS, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_JQRKJHQYDZYJS"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            LogInfoUtils.info(robotCode, ModuleEnums.BOOT, LogType.BOOT_GET_ASSETS, e.getMessage());
        }
    }

    @Override
    public void replyUpdateCloudAssetsResult(String uuid, String robotCode, Boolean result) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_PUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, String>() {{
            put("pub_name", TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_UPDATE);
            put("uuid", uuid);
            put("data", result ? "1" : "0");
        }}))));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setReceiverId(robotCode);
        messageInfo.setSenderId("goor-server");
        messageInfo.setMessageType(MessageType.ROBOT_INFO);
        messageInfo.setMessageText(JSON.toJSONString(commonInfo));
        try {
            messageSendHandleService.sendCommandMessage(true, false, robotCode, messageInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Object saveScene(Scene scene) throws Exception {
        scene.setStoreId(STORE_ID);//设置默认 store ID
        scene.setActive(Integer.valueOf(1));//新增场景默认启用
        scene.setCreateTime(new Date());//设置当前时间为创建时间
        this.save(scene);//数据库中插入这条场景记录

        this.deleteRobotAndSceneRelations(scene.getId());
        boolean flag = bindSceneAndRobotRelations(scene, null);//绑定场景与机器人之间的对应关系

        this.deleteMapAndSceneRelations(scene.getId());
        //更新缓存，删除数据库删掉的地图场景绑定关系
        CacheInfoManager.removeSceneMapRelationCache(scene.getId());
        bindSceneAndMapRelations(scene);//绑定场景与地图信息之间的对应关系
        //增加缓存
        CacheInfoManager.setSceneMapRelationCache(scene.getId(), scene.getMapSceneName());
        scene.setState(0);//状态直接置为未同步
        updateSelective(scene);
        return scene;
    }

    @Override
    public Scene storeSceneInfoToSession(String source, String sceneId, String token) throws Exception {
        Preconditions.checkArgument(sceneId != null && !"".equals(sceneId.trim()), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_QCRHFDSCENEIDZ"));
        Preconditions.checkArgument(token != null && !"".equals(token.trim()), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_QCRHFDTOKENZ"));
        log.info("传入的场景 ID 编号为 ：" + sceneId);
        Scene scene = getSceneById(Long.parseLong(sceneId));
        if (source.equals(Constant.RECORD_SCENE_SOURCE_PAD)) {
            SessionUtil.SCENE_LOADING_CACHE.put(token + ":" + Constant.SCENE_SESSION_TAG_PAD, scene);
        } else {
            SessionUtil.SCENE_LOADING_CACHE.put(token + ":" + Constant.SCENE_SESSION_TAG_PC, scene);
        }
        log.info("传入用户会话中的场景信息为：" + scene);
        return scene;
    }

    @Override
    public String getRelatedMapNameBySceneId(Long sceneId) {
        return sceneMapper.getRelatedMapNameBySceneId(sceneId);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Object updateScene(Scene scene) throws Exception {
        log.info("更新场景信息，scene=" + JSON.toJSONString(scene));
        Long sceneId = scene.getId();
        List<Robot> previousRobots = sceneMapper.findRobotBySceneId(scene.getId());
        List<Robot> nowRobots = scene.getRobots();
        List<Long> previousRobotsIds = Lists.newArrayList();
        List<Long> nowRobotsIds = Lists.newArrayList();
        if (previousRobots != null) {
            previousRobots.forEach(robot -> previousRobotsIds.add(robot.getId()));
        }
        if (nowRobots != null) {
            nowRobots.forEach(robot -> nowRobotsIds.add(robot.getId()));
        }
        // 需要取消充电桩的机器人编号
        List<Long> distinctIDS = Lists.newArrayList();
        previousRobotsIds.forEach(id->{
            if (nowRobotsIds.indexOf(id) == -1) {
                distinctIDS.add(id);
            }
        });
        //设置默认 store ID
        scene.setStoreId(STORE_ID);
        scene.setCreateTime(new Date());
        this.deleteRobotAndSceneRelations(sceneId);
        //更新场景与机器人之间的绑定关系
        boolean flag = bindSceneAndRobotRelations(scene, distinctIDS);
        //更新对应的场景信息
        updateSelective(scene);
        return scene;
    }

    private Object updateMap(Scene scene) throws Exception {
        List<Robot> robots = this.sceneMapper.findRobotBySceneId(scene.getId());
        if (robots != null && !robots.isEmpty()) {
            return updateMap(scene, robots);
        }else {
            updateSceneState(Constant.UPLOAD_FAIL, scene.getId());
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CJWBDJQR"));
        }
    }

    private Object updateMap(Scene scene, List<Robot> robots) throws Exception {
        //自动下发地图
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneName(scene.getMapSceneName(), scene.getStoreId());
        log.info("更新场景信息，mapInfos.size()=" + mapInfos.size() + ", robots.size()=" + robots.size());
        if (mapInfos.size() != 0 && robots.size() != 0) {
            log.info("场景同步地图");
            return mapSyncService.sendMapSyncMessageNew(robots, scene.getMapSceneName(), scene.getId());
        } else {
            updateSceneState(Constant.UPLOAD_FAIL, scene.getId());
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_WZDGCJGLDDTCJHCJWBDJQR"));
        }
    }

    @Override
    public Scene getSceneById(Long id) throws Exception {
        Scene scene = sceneMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(scene, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CRZDBHDCJXXBCZQJC"));
        List<Robot> list = this.sceneMapper.findRobotBySceneId(id);
        foreachList(list);
        scene.setRobots(list);
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(id, scene.getStoreId());
        if (mapInfos != null && mapInfos.size() != 0) {
            scene.setMapSceneName(mapInfos.get(0).getSceneName());
            mapInfos.forEach(mapInfo -> {
                if (StringUtil.isNotBlank(mapInfo.getPngImageLocalPath())) {
                    mapInfo.setPngImageHttpPath(DOWNLOAD_HTTP + mapInfo.getPngImageLocalPath());
                }
                if (StringUtil.isNotBlank(mapInfo.getPngDesigned())) {
                    mapInfo.setPngDesignedHttpPath(DOWNLOAD_HTTP + mapInfo.getPngDesigned());
                }
            });
            scene.setMapInfoList(mapInfos);
        }
        return scene;
    }

    @Override
    public List<MapPoint> listMapPointIdBySceneId(Long sceneId, Long storeId, Integer cloudMapPointTypeId) throws Exception {
        List<MapPoint> mapPointListDb = sceneMapper.findMapPointBySceneId(sceneId, storeId, cloudMapPointTypeId);
        return mapPointListDb;
    }

    @Override
    public int deleteSceneById(Long id) throws Exception {
        return sceneMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Scene> listScenes(WhereRequest whereRequest) throws Exception {
        List<Scene> scenes = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Scene.class, "ID DESC");
        for (Scene scene : scenes) {
            List<Robot> robotListDB = this.sceneMapper.findRobotBySceneId(scene.getId());
            List<Robot> robotList = Lists.newArrayList();
            for (int i = 0; i < robotListDB.size(); i++) {
                Robot robot = robotListDB.get(i);
                List<RobotMapZipXREF> robotMapZipXREFList = robotMapZipXREFService.findByRobotId(robot.getId(), scene.getId());
;                boolean result = (robotMapZipXREFList == null || robotMapZipXREFList.size() <= 0) ? false : robotMapZipXREFList.get(0).isSuccess();
                robot.setMapSyncResult(result);
                robotList.add(robot);
            }

            scene.setRobots(robotList);//设置绑定的机器人信息
            List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(scene.getId(), scene.getStoreId());
            if (mapInfos != null && mapInfos.size() != 0) {
                scene.setMapSceneName(mapInfos.get(0).getSceneName());//设置绑定的场景名城
            }
        }
        return scenes;
    }

    @Override
    public int insertSceneAndMapRelations(Long sceneId, String mapSceneName) throws Exception {
        return this.sceneMapper.insertSceneAndMapRelations(sceneId, mapSceneName);
    }

    @Override
    public int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds) throws Exception {
        return this.sceneMapper.insertSceneAndRobotRelations(sceneId, robotIds);
    }

    @Override
    public Object sendSyncMapMessageToRobots(Long sceneId) throws Exception {
        Scene scene = this.sceneMapper.selectByPrimaryKey(sceneId);
        String mapSceneName = sceneMapper.getRelatedMapNameBySceneId(sceneId);
        scene.setMapSceneName(mapSceneName);
        sceneMapper.setSceneState(scene.getName(), scene.getStoreId(), 0);//将状态更改为正在上传
        return updateMap(scene);
    }

    /**
     * 遍历赋机器人在线字段
     *
     * @param list
     */
    private static void foreachList(List<Robot> list) {
        list.forEach(robot -> {
            Boolean flag = CacheInfoManager.getRobotOnlineCache(robot.getCode());
            if (flag == null) {
                flag = false;
            }
            robot.setOnline(flag);
        });
    }

    @Override
    public Object sendSyncMapMessageToSpecialRobots(Map<String, Object> params) throws Exception {
        Long sceneId = Long.valueOf(String.valueOf(Preconditions.checkNotNull(params.get("sceneId"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CJIDBYXWK"))));
        List<Long> robotIds = (List<Long>) Preconditions.checkNotNull(params.get("robotIds"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CRDJQRIDBHXXSZBNWK"));
        Preconditions.checkArgument(robotIds.size() != 0, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CRDJQRBHSZXXBKYWK"));
        Scene scene = this.sceneMapper.selectByPrimaryKey(sceneId);
        List<Robot> robots = this.sceneMapper.findRobotBySceneIdAndRobotIds(params);
        foreachList(robots);
        String mapSceneName = sceneMapper.getRelatedMapNameBySceneId(sceneId);
        scene.setMapSceneName(mapSceneName);
        sceneMapper.setSceneState(scene.getName(), scene.getStoreId(), 0);//将状态更改为正在上传
        return updateMap(scene, robots);
    }

    @Override
    public void deleteRobotAndSceneRelations(Long sceneId) throws Exception {
        this.sceneMapper.deleteRobotAndSceneRelations(sceneId);
    }

    @Override
    public void deleteMapAndSceneRelations(Long sceneId) throws Exception {
        this.sceneMapper.deleteMapAndSceneRelations(sceneId);
    }

    @Override
    public int checkRobot(Long robotId) throws Exception {
        return sceneMapper.checkRobot(robotId);
    }

    private static final String MAP_INFO_ERROR_MESSAGE = "goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_ZDDDTCJBCZHZYJBBDDYDCJQZXXZ";

    @Override
    public void bindSceneAndMapRelations(Scene scene) throws Exception {
        try {
            Long sceneId = Preconditions.checkNotNull(scene.getId());
            String sceneName = Preconditions.checkNotNull(scene.getName());
            String mapSceneName = Preconditions.checkNotNull(scene.getMapSceneName());
            //&& this.sceneMapper.checkMapInfo(mapSceneName, scene.getStoreId()) == 0
            if (this.sceneMapper.checkMapLegal(mapSceneName, scene.getStoreId()) > 0) {
                //保证场景名城合法且没有绑定云端场景
                this.insertSceneAndMapRelations(scene.getId(), mapSceneName);
            } else {
                throw new Exception(localeMessageSourceService.getMessage(MAP_INFO_ERROR_MESSAGE));
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private static final String ROBOT_ERROR_MESSAGE = "goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_CRDJQRXXBCZHZJQRYJBBDDYDCJQZXXZ";

    @Override
    public boolean bindSceneAndRobotRelations(Scene scene,  List<Long> distinctIDS) throws Exception {
        boolean flag = false;
        try {
            lock.lock();//操作开始前先上锁
            Long sceneId = scene.getId();
            List<Robot> robots = scene.getRobots();//可能为空或者为一个空数组
            List<Long> ids = new ArrayList<>();//真正需要以及将会插入到关系表中的 ID 信息数据
            // 当前更新对应的机器人信息
            List<Robot> currentRobots = scene.getRobots();
            // 旧的机器人列表信息，如果取消了的话，需要解绑对应的充电桩点
            for (Robot robot : robots) {
                if (this.sceneMapper.checkRobotLegal(robot.getId()) > 0 && this.sceneMapper.checkRobot(robot.getId()) == 0) {
                    //机器人合法并且机器人没有绑定到已有场景的条件
//                    robotService.bindChargerMapPoint(robot.getId(), null);
                    ids.add(robot.getId());
                }
                if (distinctIDS != null && distinctIDS.size()!=0){
                    distinctIDS.forEach( id -> robotService.bindChargerMapPoint(id, null) );
                }
            }
            if (ids.size() != 0) {
                //如果有效序号ids的集合内容不为空，则插入新的关系信息，否则不执行任何操作
                //不能抛出异常信息，因为仙子阿允许绑定空元素
                flag = true;//表示有实际的机器人进行地图下发操作
                this.insertSceneAndRobotRelations(scene.getId(), ids);
            }
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                log.info(" - - - - 当前操作正处在事务中 - - - - ");
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronizationAdapter() {
                            @Override
                            public void afterCompletion(int status) {
                                switch (status) {
                                    case STATUS_COMMITTED:
                                        log.info(" - - - - 事务已提交 - - - - ");
                                        break;
                                    case STATUS_ROLLED_BACK:
                                        log.info(" - - - - 事务已回滚 - - - - ");
                                        break;
                                    case STATUS_UNKNOWN:
                                        log.info(" - - - - 事务状态为止 - - - - ");
                                        break;
                                    default:
                                        break;
                                }
                                lock.unlock();
                            }
                        });
            } else {
                //事务执行异常，整体事务需要进行回滚操作
                log.info(" ~ ~ ~ ~ 当前操作没有实际事务，系统异常，回滚事务 ~ ~ ~ ~ ");
                throw new Exception(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_DQCZMYSJSWXTYCHGSW"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return flag;
    }

    /**
     * tang lin 调用的接口，判断指定的地图包是否有更新
     *
     * @param mapSceneName
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkSceneIsNeedToBeUpdated(String mapSceneName, String storeId) throws Exception {
        //标明状态为可更新状态
        if (this.sceneMapper.checkMapInfo(mapSceneName, Long.parseLong(storeId)) != 0) {
            this.sceneMapper.setSceneState(mapSceneName, Long.parseLong(storeId), 3);
        }
        return true;
    }

    @Override
    public void updateSceneState(int state, Long sceneId) throws Exception {
        Preconditions.checkArgument(sceneId != null, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_scene_service_impl_SceneServiceImpl_java_GGCJZTWSCCGSCJIDBHQSQJCDM"));
        Scene scene = sceneMapper.selectByPrimaryKey(sceneId);
        scene.setState(state);
        sceneMapper.updateByPrimaryKeySelective(scene);
    }
}