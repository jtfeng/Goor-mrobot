package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.RobotMapZipXREF;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/15.
 */
public interface RobotMapZipXREFService {

    void save(RobotMapZipXREF robotMapZipXREF);

    List<RobotMapZipXREF> findByRobotId(Long robotId, Long sceneId);

    /**
     * 根据场景ID将该ID以前的数据置为已删除
     * @param sceneId
     */
    void removeBySceneId(Long sceneId);
}
