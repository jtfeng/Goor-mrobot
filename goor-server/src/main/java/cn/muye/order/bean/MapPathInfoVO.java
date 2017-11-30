package cn.muye.order.bean;

import java.util.List;

/**
 * Created by Selim on 2017/11/29.
 * 为显示图列表集合点
 */
public class MapPathInfoVO {

    private MapPointVO originMapPoint;   //原点

    private MapPointVO startMapPoint;  //开始点

    private MapPointVO endMapPoint;    //结束点

    private List<MapPathInfoDetailVO> mapDetailList;  //详细细节路径数据

    public MapPointVO getOriginMapPoint() {
        return originMapPoint;
    }

    public void setOriginMapPoint(MapPointVO originMapPoint) {
        this.originMapPoint = originMapPoint;
    }

    public MapPointVO getStartMapPoint() {
        return startMapPoint;
    }

    public void setStartMapPoint(MapPointVO startMapPoint) {
        this.startMapPoint = startMapPoint;
    }

    public MapPointVO getEndMapPoint() {
        return endMapPoint;
    }

    public void setEndMapPoint(MapPointVO endMapPoint) {
        this.endMapPoint = endMapPoint;
    }

    public List<MapPathInfoDetailVO> getMapDetailList() {
        return mapDetailList;
    }

    public void setMapDetailList(List<MapPathInfoDetailVO> mapDetailList) {
        this.mapDetailList = mapDetailList;
    }
}
