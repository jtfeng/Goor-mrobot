package cn.muye.dijkstra;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;

import java.util.ArrayList;
import java.util.List;


public class RoadPathMaps {
    private static final Long DEFAULT_DISTANCE = 1L;
    private Graph g = new Graph();
    public RoadPathMaps(){}

    /*@
    初始化：载入所有固定路径，且列表里面的路径是根据START_POINT进行排序的
     */
    public void init(List<RoadPath> roadPathList) {
        Long startPoint = -1L;
        List<Long> startPointList = new ArrayList<Long>();
        List<Vertex> vertexList = null;
        for (RoadPath path: roadPathList) {
            //id 变更，更新上一个id的路径到图中，并重新申请相关资源
            if (!startPoint.equals(path.getStartPoint())) {
                //更新上一个id的路径到图中
                if (!startPoint.equals(-1L)) {
                    g.addVertex(startPoint,vertexList);
                    startPointList.add(startPoint);
                }
                // 重新申请资源
                startPoint = path.getStartPoint();
                vertexList = new ArrayList<Vertex>();
            }

            //TODO // distance 使用默认值
//            vertexList.add(new Vertex(path.getEndPoint(), DEFAULT_DISTANCE));
            vertexList.add(new Vertex(path.getEndPoint(), path.getWeight()));
            // 后续每条路径有值后再进行替换
//            Long distance = (path.getWeight() == null)? DEFAULT_DISTANCE : path.getWeight();
//            vertexList.add(new Vertex(path.getEndPoint(), distance.intValue()));
        }

        //最后一个路径ID的相关数据更新到图中
        if (!startPoint.equals(-1L)) {
            g.addVertex(startPoint, vertexList);
            startPointList.add(startPoint);
        }

        //对于某个没有作为任意一条路径的起始点的点，也需要进行初始化
        for (RoadPath path: roadPathList) {
            Long endPoint = path.getEndPoint();
            if (startPointList.contains(endPoint)) {
                continue;
            }
            g.addVertex(endPoint, new ArrayList<Vertex>());
        }
    }

    public RoadPathResult getShortestPath(Long startPointId, Long finishPointIdId) {
        return g.getShortestPath(startPointId, finishPointIdId);
    }

    public RoadPathResult getShortestPath(MapPoint startPoint, MapPoint finishPoint) {
        return g.getShortestPath(startPoint.getId(), finishPoint.getId());
    }
}
