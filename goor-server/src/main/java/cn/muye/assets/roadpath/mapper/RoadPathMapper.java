package cn.muye.assets.roadpath.mapper;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.muye.util.MyMapper;

public interface RoadPathMapper extends MyMapper<RoadPath> {

    String findMapSceneName(Long sceneId);

}