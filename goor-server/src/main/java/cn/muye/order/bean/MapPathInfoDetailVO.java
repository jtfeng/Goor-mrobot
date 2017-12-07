package cn.muye.order.bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by Selim on 2017/11/29.
 */
public class MapPathInfoDetailVO {

    private Map<String, Object> mapInfo = Maps.newHashMap();  //记录地图信息

    private List<MapPointVO> mapPointList = Lists.newArrayList();   //点集合

    public Map<String, Object> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(Map<String, Object> mapInfo) {
        this.mapInfo = mapInfo;
    }

    public List<MapPointVO> getMapPointList() {
        return mapPointList;
    }

    public void setMapPointList(List<MapPointVO> mapPointList) {
        this.mapPointList = mapPointList;
    }
}
