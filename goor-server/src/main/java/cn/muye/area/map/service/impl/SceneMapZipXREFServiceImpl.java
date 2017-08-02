package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.SceneMapZipXREF;
import cn.muye.area.map.mapper.SceneMapZipXREFMapper;
import cn.muye.area.map.service.SceneMapZipXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/2.
 */
@Service
public class SceneMapZipXREFServiceImpl implements SceneMapZipXREFService {

    @Autowired
    private SceneMapZipXREFMapper sceneMapZipXREFMapper;

    @Override
    public long save(SceneMapZipXREF sceneMapZipXREF) {
        return sceneMapZipXREFMapper.insert(sceneMapZipXREF);
    }

    @Override
    public List<SceneMapZipXREF> list(Long mapZipId) {
        Example example = new Example(SceneMapZipXREF.class);
        example.createCriteria().andCondition("MAP_ZIP_ID=" + mapZipId);
        return sceneMapZipXREFMapper.selectByExample(example);
    }

    @Override
    public long getMapZipId(String sceneName, String mapName) {
        Example example = new Example(SceneMapZipXREF.class);
        example.createCriteria().andCondition("SCENE_NAME= '" + sceneName + "'").andCondition("MAP_NAME= '" + mapName + "'");
         List<SceneMapZipXREF> sceneMapZipXREFList =  sceneMapZipXREFMapper.selectByExample(example);
         if (sceneMapZipXREFList.size() >0){
             return sceneMapZipXREFList.get(0).getMapZipId();
         }
        return 0;
    }

    @Override
    public void delete(Long mapZipId) {
        Example example = new Example(SceneMapZipXREF.class);
        example.createCriteria().andCondition("MAP_ZIP_ID=" + mapZipId);
        sceneMapZipXREFMapper.deleteByExample(example);
    }

    @Override
    public void updateDeleteFlag(Long mapZipId, int deleteFlag) {
        sceneMapZipXREFMapper.updateDeleteFlag(mapZipId, deleteFlag);
    }

    @Override
    public void delete(int deleteFlag) {
        Example example = new Example(SceneMapZipXREF.class);
        example.createCriteria().andCondition("DELETE_FLAG=" + deleteFlag);
        sceneMapZipXREFMapper.deleteByExample(example);
    }
}
