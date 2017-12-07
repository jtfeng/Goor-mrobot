package cn.muye.order.bean;

/**
 * Created by Selim on 2017/11/29.
 * 为显示图列表集合点
 */
public class MapPathInfoVO {

    private MapPointVO startMapPoint;  //开始点

    private MapPointVO endMapPoint;    //结束点

    private MapPathInfoDetailVO mapPathInfoDetail;  //详细细节路径数据

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

    public MapPathInfoDetailVO getMapPathInfoDetail() {
        return mapPathInfoDetail;
    }

    public void setMapPathInfoDetail(MapPathInfoDetailVO mapPathInfoDetail) {
        this.mapPathInfoDetail = mapPathInfoDetail;
    }
}
