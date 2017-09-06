package cn.muye.assets.scene.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapZipMapper;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.servlet.http.HttpServletRequest;
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
    private RobotMapper robotMapper;
    @Autowired
    private MapZipMapper mapZipMapper;
    @Autowired
    private RobotMapZipXREFService robotMapZipXREFService;
    //保存添加场景与机器人之间的关系时候，需要加锁，以免事务未提交读取到脏数据
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public List<Scene> list() throws Exception {
        return sceneMapper.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object saveScene(Scene scene, HttpServletRequest request) throws Exception {
        scene.setStoreId(STORE_ID);//设置默认 store ID
        scene.setCreateTime(new Date());//设置当前时间为创建时间
        if (scene.getRobots() != null && scene.getRobots().size() != 0) {
            scene.setState(0);//代表正在上传
        } else {
            scene.setState(1);//代表上传成功
        }
        this.save(scene, request);//数据库中插入这条场景记录

        this.deleteRobotAndSceneRelations(scene.getId());
        bindSceneAndRobotRelations(scene);//绑定场景与机器人之间的对应关系

        this.deleteMapAndSceneRelations(scene.getId());
        bindSceneAndMapRelations(scene);//绑定场景与地图信息之间的对应关系

        Object taskResult = null;
        if (scene.getRobots() != null && scene.getRobots().size() != 0) {
            //自动下发地图
            List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneName(scene.getMapSceneName(), scene.getStoreId());
            List<Robot> robots = new ArrayList<>();
            for (Robot robot : scene.getRobots()) {
                robots.add(robotMapper.selectByPrimaryKey(robot.getId()));
            }
            if (mapInfos.size() != 0 && robots.size() != 0) {
                log.info("场景同步地图");
                taskResult = mapSyncService.sendMapSyncMessage(robots, mapZipMapper.selectByPrimaryKey(mapInfos.get(0).getMapZipId()), scene.getId());
            }
        }
        return taskResult;
    }

    @Override
    public Scene storeSceneInfoToSession(String source, String sceneId, String token) throws Exception {
        Preconditions.checkArgument(sceneId != null && !"".equals(sceneId.trim()), "请传入合法的 sceneId 值");
        log.info("传入的场景 ID 编号为 ：" + sceneId);
        Scene scene = getSceneById(Long.parseLong(sceneId));
        if (source.equals(Constant.RECORD_SCENE_SOURCE_PAD)) {
            SessionUtil.SCENE_LOADING_CACHE.put((token != null ? token : null)+":"+Constant.SCENE_SESSION_TAG_PAD, scene);
        } else {
            SessionUtil.SCENE_LOADING_CACHE.put((token != null ? token : null)+":"+Constant.SCENE_SESSION_TAG_PC, scene);
        }
        log.info("传入用户会话中的场景信息为：" + scene);
        return scene;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object updateScene(Scene scene) throws Exception {
        log.info("更新场景信息，scene="+ JSON.toJSONString(scene));
        Scene existScene = this.sceneMapper.selectByPrimaryKey(scene.getId());
        scene.setStoreId(STORE_ID);//设置默认的门店编号
        scene.setCreateTime(new Date());
        if (scene.getRobots() != null && scene.getRobots().size() != 0) {
            scene.setState(0);//代表正在上传
        } else {
            scene.setState(1);//代表上传失败
        }

        this.deleteRobotAndSceneRelations(scene.getId());
        bindSceneAndRobotRelations(scene);//更新场景与机器人之间的绑定关系

        updateSelective(scene) ;//更新对应的场景信息
        Object taskResult = null;
        if (scene.getRobots() != null && scene.getRobots().size() != 0) {
            //自动下发地图
            log.info("更新场景信息，scene.getMapSceneName()=" + scene.getMapSceneName() + ", scene.getStoreId()" + scene.getStoreId());
            List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneName(scene.getMapSceneName(), scene.getStoreId());
            log.info("更新场景信息，mapInfos=" + JSON.toJSONString(mapInfos));
            List<Robot> robots = new ArrayList<>();
            for (Robot robot : scene.getRobots()) {
                robots.add(robotMapper.selectByPrimaryKey(robot.getId()));
            }
            log.info("更新场景信息，mapInfos.size()=" + mapInfos.size() + ", robots.size()=" + robots.size());
            if (mapInfos.size() != 0 && robots.size() != 0) {
                log.info("场景同步地图");
                taskResult = mapSyncService.sendMapSyncMessage(robots, mapZipMapper.selectByPrimaryKey(mapInfos.get(0).getMapZipId()), scene.getId());
            }
        }
        return taskResult;
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
        return  sceneMapper.deleteByPrimaryKey(id) ;
    }

    @Override
    public List<Scene> listScenes(WhereRequest whereRequest) throws Exception {
        List<Scene> scenes = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(),Scene.class,"ID DESC");
        for (Scene scene : scenes){
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
        List<Robot> robots     = this.sceneMapper.findRobotBySceneId(sceneId);
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(sceneId, scene.getStoreId());
        if (robots.size() == 0 || mapInfos.size() == 0){
            return null;
        }
        Preconditions.checkNotNull(mapInfos);
        Preconditions.checkArgument(mapInfos.size()!=0, "该场景没有绑定地图，请绑定地图后重试!");
        MapZip mapZip = this.mapZipMapper.selectByPrimaryKey(mapInfos.get(0).getMapZipId());
        sceneMapper.setSceneState(scene.getName(), scene.getStoreId(), 0);//将状态更改为正在上传
        return mapSyncService.sendMapSyncMessage(robots, mapZip, sceneId);
    }

    /**
     * 遍历赋机器人在线字段
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
        List<Long> robotIds = (List<Long>)Preconditions.checkNotNull(params.get("robotIds"), "传入的机器人 ID 编号信息数组不能为空");
        Preconditions.checkArgument(robotIds.size() != 0, "传入的机器人编号数组信息不可以为空");
        Scene scene = this.sceneMapper.selectByPrimaryKey(sceneId);
        List<Robot> robots     = this.sceneMapper.findRobotBySceneIdAndRobotIds(params);
        foreachList(robots);
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(sceneId, scene.getStoreId());
        if (robots.size() == 0 || mapInfos.size() == 0){
            return null;
        }
        Preconditions.checkNotNull(mapInfos);
        Preconditions.checkArgument(mapInfos.size()!=0, "该场景没有绑定地图，请绑定地图后重试!");
        MapZip mapZip = this.mapZipMapper.selectByPrimaryKey(mapInfos.get(0).getMapZipId());
        sceneMapper.setSceneState(scene.getName(), scene.getStoreId(), 0);//将状态更改为正在上传
        return mapSyncService.sendMapSyncMessage(robots, mapZip, sceneId);
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

    private static final String MAP_INFO_ERROR_MESSAGE = "指定的地图场景不存在或者已经被绑定到云端场景，请重新选择!" ;

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
        }catch (Exception e){
            throw e;
        }
    }

    private static final String ROBOT_ERROR_MESSAGE = "传入的机器人信息不存在或者机器人已经被绑定到云端场景，请重新选择!" ;

    @Override
    public void bindSceneAndRobotRelations(Scene scene) throws Exception {
        try {
            lock.lock();//操作开始前先上锁
            Long sceneId = scene.getId();
            List<Robot> robots = scene.getRobots();//可能为空或者为一个空数组
            List<Long> ids = new ArrayList<>();//真正需要以及将会插入到关系表中的 ID 信息数据
            for (Robot robot : robots) {
                if (this.sceneMapper.checkRobotLegal(robot.getId()) >0 && this.sceneMapper.checkRobot(robot.getId()) == 0) {
                    //机器人合法并且机器人没有绑定到已有场景的条件
                    robotService.bindChargerMapPoint(robot.getId(), null);
                    ids.add(robot.getId());
                }
            }
            if (ids.size() != 0) {
                //如果有效序号ids的集合内容不为空，则插入新的关系信息，否则不执行任何操作
                //不能抛出异常信息，因为仙子阿允许绑定空元素
                this.insertSceneAndRobotRelations(scene.getId(), ids);
            }
            if(TransactionSynchronizationManager.isActualTransactionActive()){
                log.info(" - - - - 当前操作正处在事务中 - - - - ");
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronizationAdapter(){
                            @Override
                            public void afterCompletion(int status) {
                                switch (status){
                                    case STATUS_COMMITTED:
                                        log.info(" - - - - 事务已提交 - - - - ");
                                        break;
                                    case STATUS_ROLLED_BACK:
                                        log.info(" - - - - 事务已回滚 - - - - ");
                                        break;
                                    case STATUS_UNKNOWN:
                                        log.info(" - - - - 事务状态为止 - - - - ");
                                        break;
                                }
                                lock.unlock();
                            }
                        });
            }else{
                //事务执行异常，整体事务需要进行回滚操作
                log.info(" ~ ~ ~ ~ 当前操作没有实际事务，系统异常，回滚事务 ~ ~ ~ ~ ");
                throw new Exception("当前操作没有实际事务，系统异常，回滚事务");
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * tang lin 调用的接口，判断指定的地图包是否有更新
     * @param mapSceneName
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkSceneIsNeedToBeUpdated(String mapSceneName, String storeId, Scene.SCENE_STATE state, Long ... sceneId) throws Exception {
        Preconditions.checkNotNull(state);
        if (Scene.SCENE_STATE.UPDATE_STATE.equals(state)) {
            //标明状态为可更新状态
            if (this.sceneMapper.checkMapInfo(mapSceneName, Long.parseLong(storeId)) != 0) {
                this.sceneMapper.setSceneState(mapSceneName, Long.parseLong(storeId), 3);
            }
        }
        if (Scene.SCENE_STATE.UPLOAD_SUCCESS.equals(state)){
            //表明状态为上传成功状态(需要针对某个具体场景)
//            this.sceneMapper.setSceneState(mapSceneName, Long.parseLong(storeId), 1);
            Preconditions.checkArgument(sceneId != null && sceneId.length == 1, "更改场景状态为上传成功时,场景ID编号缺失,请检查代码!");
            this.sceneMapper.setSceneStateForUpload(sceneId[0],1);
        }
        return true;
    }
}