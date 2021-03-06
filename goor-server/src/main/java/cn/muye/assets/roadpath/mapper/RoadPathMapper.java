package cn.muye.assets.roadpath.mapper;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathPoint;
import cn.muye.util.MyMapper;

import java.util.List;

public interface RoadPathMapper extends MyMapper<RoadPath> {

    String findMapSceneName(Long sceneId);

    List<RoadPathPoint> findBeginRoadPathPoint(Long roadPathId);

    List<RoadPathPoint> findSpecifyRoadPathPoint(Long roadPathId, Long pointId);

    void deleteRoadPathPointsByPathId(Long roadPathId);

    List<RoadPathPoint> findRoadPathPointByRoadPath(Long roadPathId);

}