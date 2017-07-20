package cn.muye.assets.scene.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.assets.rfidbracelet.controller.RfidBraceletController;
import cn.muye.assets.rfidbracelet.mapper.RfidBraceletMapper;
import cn.muye.assets.rfidbracelet.service.RfidBraceletService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    private SceneMapper sceneMapper;

    @Override
    public List<Scene> list() {
        return sceneMapper.selectAll();
    }

    @Override
    public int save(Scene scene) {
        scene.setStoreId(STORE_ID);
        scene.setCreateTime(new Date());
        return sceneMapper.insert(scene);
    }

    @Override
    public Scene getById(Long id) {
        Scene scene = sceneMapper.selectByPrimaryKey(id);
        //取得所有的机器人信息
        scene.setRobots(this.sceneMapper.findRobotBySceneId(id));
        //取得所有的地图信息
        scene.setMapInfos(this.sceneMapper.findMapBySceneId(id));
        return scene;
    }

    @Override
    public int update(Scene scene) {
        scene.setStoreId(STORE_ID);
        if (scene.getCreateTime() == null){
            scene.setCreateTime(new Date());
        }
        return sceneMapper.updateByPrimaryKey(scene) ;
    }

    @Override
    public int deleteById(Long id) {
        Scene scene = findById(id);
        return  sceneMapper.deleteByPrimaryKey(id) ;
    }

    @Override
    public List<Scene> listScenes(WhereRequest whereRequest) {
        List<Scene> scenes = listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(),Scene.class,"ID DESC");
        for (Scene scene : scenes){
            //分别将绑定的机器人信息和地图数据信息取回
            scene.setRobots(this.sceneMapper.findRobotBySceneId(scene.getId()));
            scene.setMapInfos(this.sceneMapper.findMapBySceneId(scene.getId()));
        }
        return scenes;
    }

    @Override
    public Scene updateAliasName(Long sceneId, String aliasName) {
        //查询原本对象，然后更新场景中的别名属性
        Scene scene = this.sceneMapper.selectByPrimaryKey(sceneId);
        scene.setAliasName(aliasName);
        this.sceneMapper.updateByPrimaryKey(scene);
        return scene;
    }

    @Override
    public int insertSceneAndMapRelations(Long sceneId, List<Long> mapIds) {
        return this.sceneMapper.insertSceneAndMapRelations(sceneId, mapIds);
    }

    @Override
    public int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds) {
        return this.sceneMapper.insertSceneAndRobotRelations(sceneId, robotIds);
    }

    @Override
    public void sendSyncMapMessageToRobots(Long sceneId) {
        List<Robot> robots = this.sceneMapper.findRobotBySceneId(sceneId);
        List<MapInfo> mapInfos = this.sceneMapper.findMapBySceneId(sceneId);
        if (robots.size() == 0 || mapInfos.size() == 0){
            log.info("当前场景所绑定的 地图或者机器人不存在 ，不能正常绑定，程序退出。");
            return;
        }
        //调用接口，发送同步地图资源到机器人的信息
        MapZip mapZip = new MapZip();
        mapZip.setId(mapInfos.get(0).getMapZipId());
        mapSyncService.sendMapSyncMessage(robots, mapZip);
    }

    @Override
    public void deleteRobotAndSceneRelations(Long sceneId) {
        this.sceneMapper.deleteRobotAndSceneRelations(sceneId);
    }

    @Override
    public void deleteMapAndSceneRelations(Long sceneId) {
        this.sceneMapper.deleteMapAndSceneRelations(sceneId);
    }
}