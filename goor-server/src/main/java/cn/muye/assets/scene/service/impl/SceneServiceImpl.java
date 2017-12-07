package cn.muye.assets.scene.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapInfoMapper;
import cn.muye.area.map.mapper.MapZipMapper;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
    private MapInfoMapper mapInfoMapper;
    @Autowired
    private RobotMapZipXREFService robotMapZipXREFService;
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
    public String getRobotStartAssets() {
        Map baseData = new HashMap();
        List sceneList = new ArrayList();
        // 获取所有的场景信息
        List<Scene> scenes = sceneMapper.selectAll();
        for (Scene scene : scenes) {
            //依次遍历场景并且获取场景下对应的地图和站信息
            Map eachScene = new HashMap();
            eachScene.put("id", scene.getId());
            eachScene.put("name", scene.getName());
            //设置站
            Example stationExample = new Example(Station.class);
            stationExample.createCriteria().andCondition("SCENE_ID =", scene.getId());
            eachScene.put("station", stationMapper.selectByExample(stationExample));
            //
            //设置地图
            Example mapExample = new Example(MapInfo.class);
            mapExample.createCriteria().andCondition("SCENE_NAME =", scene.getName());
            eachScene.put("map", mapInfoMapper.selectByExample(mapExample));
            //
            sceneList.add(eachScene);
        }
        baseData.put("scene", sceneList);
        return JSONObject.toJSONString(baseData);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Object saveScene(Scene scene) throws Exception {
        scene.setStoreId(STORE_ID);//设置默认 store ID
        scene.setCreateTime(new Date());//设置当前时间为创建时间
        this.save(scene);//数据库中插入这条场景记录

        this.deleteRobotAndSceneRelations(scene.getId());
        boolean flag = bindSceneAndRobotRelations(scene, null);//绑定场景与机器人之间的对应关系

        this.deleteMapAndSceneRelations(scene.getId());
        bindSceneAndMapRelations(scene);//绑定场景与地图信息之间的对应关系

        if (flag) {
            // 实际有机器人需要进行地图下发操作
            scene.setState(0);
        } else {
            scene.setState(1);
        }
        updateSelective(scene);

        Object taskResult = updateMap(scene);
        return taskResult;
    }

    @Override
    public Scene storeSceneInfoToSession(String source, String sceneId, String token) throws Exception {
        Preconditions.checkArgument(sceneId != null && !"".equals(sceneId.trim()), "请传入合法的 sceneId 值");
        Preconditions.checkArgument(token != null && !"".equals(token.trim()), "请传入合法的 token 值");
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
        if (flag) {
            // 实际有机器人需要进行地图下发操作
            scene.setState(0);
        } else {
            scene.setState(1);
        }
        //更新对应的场景信息
        updateSelective(scene);
        Object taskResult = updateMap(scene);
        return taskResult;
    }

    private Object updateMap(Scene scene) throws Exception {
        List<Robot> robots = this.sceneMapper.findRobotBySceneId(scene.getId());
        if (robots != null && !robots.isEmpty()) {
            return updateMap(scene, robots);
        }else {
            updateSceneState(Constant.UPLOAD_FAIL, scene.getId());
            return AjaxResult.failed("场景无绑定机器人");
        }
    }

    private Object updateMap(Scene scene, List<Robot> robots) throws Exception {
        //自动下发地图
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneName(scene.getMapSceneName(), scene.getStoreId());
        log.info("更新场景信息，mapInfos.size()=" + mapInfos.size() + ", robots.size()=" + robots.size());
        if (mapInfos.size() != 0 && robots.size() != 0) {
            log.info("场景同步地图");
            return mapSyncService.sendMapSyncMessage(robots, mapZipMapper.selectByPrimaryKey(mapInfos.get(0).getMapZipId()), scene.getId());
            //TODO 20171130 Artemis支持选择场景上传地图后需要放开次方法进行联调
//            return mapSyncService.sendMapSyncMessageNew(robots, scene.getMapSceneName(), scene.getId());
        }else {
            updateSceneState(Constant.UPLOAD_FAIL, scene.getId());
            return AjaxResult.failed("未找到该场景关联的地图场景或场景无绑定机器人");
        }
    }

    @Override
    public Scene getSceneById(Long id) throws Exception {
        Scene scene = sceneMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(scene, "传入指定编号的场景信息不存在，请检查!");
        List<Robot> list = this.sceneMapper.findRobotBySceneId(id);
        foreachList(list);
        scene.setRobots(list);
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(id, scene.getStoreId());
        if (mapInfos != null && mapInfos.size() != 0) {
            scene.setMapSceneName(mapInfos.get(0).getSceneName());
            mapInfos.forEach(mapInfo -> {
                if (mapInfo.getPngImageLocalPath() != null) {
                    mapInfo.setPngImageHttpPath(DOWNLOAD_HTTP + mapInfo.getPngImageLocalPath());
                }
                if (mapInfo.getPngDesigned() != null) {
                    mapInfo.setPngDesignedHttpPath(DOWNLOAD_HTTP + mapInfo.getPngDesigned());
                }
            });
            scene.setMapInfoList(mapInfos);
        }
        return scene;
    }

    @Override
    public List<MapPoint> listMapPointIdBySceneId(Long sceneId, Long storeId, Long cloudMapPointTypeId) throws Exception {
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
                List<RobotMapZipXREF> robotMapZipXREFList = robotMapZipXREFService.findByRobotId(robot.getId());
                boolean result = (robotMapZipXREFList == null || robotMapZipXREFList.size() <= 0) ? true : robotMapZipXREFList.get(0).isSuccess();
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
        Long sceneId = Long.valueOf(String.valueOf(Preconditions.checkNotNull(params.get("sceneId"), "场景 ID 不允许为空!")));
        List<Long> robotIds = (List<Long>) Preconditions.checkNotNull(params.get("robotIds"), "传入的机器人 ID 编号信息数组不能为空");
        Preconditions.checkArgument(robotIds.size() != 0, "传入的机器人编号数组信息不可以为空");
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

    private static final String MAP_INFO_ERROR_MESSAGE = "指定的地图场景不存在或者已经被绑定到云端场景，请重新选择!";

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
                throw new Exception(MAP_INFO_ERROR_MESSAGE);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private static final String ROBOT_ERROR_MESSAGE = "传入的机器人信息不存在或者机器人已经被绑定到云端场景，请重新选择!";

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
                throw new Exception("当前操作没有实际事务，系统异常，回滚事务");
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
        Preconditions.checkArgument(sceneId != null, "更改场景状态为上传成功时,场景ID编号缺失,请检查代码!");
        Scene scene = sceneMapper.selectByPrimaryKey(sceneId);
        scene.setState(state);
        sceneMapper.updateByPrimaryKeySelective(scene);
    }
}