package cn.muye.assets.scene.service.impl;

import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.rfidbracelet.controller.RfidBraceletController;
import cn.muye.assets.rfidbracelet.mapper.RfidBraceletMapper;
import cn.muye.assets.rfidbracelet.service.RfidBraceletService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private SceneMapper sceneMapper;

    @Override
    public List<Scene> list() {
        return sceneMapper.selectAll();
    }

    @Override
    public int save(Scene scene) {
        return sceneMapper.insert(scene);
    }

    @Override
    public Scene getById(Long id) {
        return sceneMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Scene scene) {
        return sceneMapper.updateByPrimaryKey(scene) ;
    }

    @Override
    public int deleteById(Long id) {
        Scene scene = findById(id);
        return  sceneMapper.deleteByPrimaryKey(id) ;
    }

    @Override
    public List<Scene> listScenes(WhereRequest whereRequest) {
        return listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(),Scene.class,"ID DESC");
    }
}