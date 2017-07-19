package cn.muye.assets.scene.service;

import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by admin on 2017/7/3.
 */
public interface SceneService extends BaseService<Scene> {

    List<Scene> list();

    int save(Scene scene);

    Scene getById(Long id);

    int update(Scene scene);

    int deleteById(Long id);

    List<Scene> listScenes(WhereRequest whereRequest);
}