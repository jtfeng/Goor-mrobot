package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.area.point.MapPoint;

import java.util.List;

public class RoadPathDetail extends RoadPath {

    private MapPoint start;
    private MapPoint end;
    private List<MapPoint> relatePoints;
    private RoadPathLock roadPathLock;

    public MapPoint getStart() {
        return start;
    }

    public void setStart(MapPoint start) {
        this.start = start;
    }

    public MapPoint getEnd() {
        return end;
    }

    public void setEnd(MapPoint end) {
        this.end = end;
    }

    public List<MapPoint> getRelatePoints() {
        return relatePoints;
    }

    public void setRelatePoints(List<MapPoint> relatePoints) {
        this.relatePoints = relatePoints;
    }

    public RoadPathLock getRoadPathLock() {
        return roadPathLock;
    }

    public void setRoadPathLock(RoadPathLock roadPathLock) {
        this.roadPathLock = roadPathLock;
    }

    @Override
    public String toString() {
        return "RoadPathDetail{" +
                "start=" + start +
                ", end=" + end +
                ", relatePoints=" + relatePoints +
                ", roadPathLock=" + roadPathLock +
                '}';
    }
}
