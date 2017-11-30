package cn.muye.order.bean;

import java.util.List;

/**
 * Created by Selim on 2017/11/29.
 */
public class MapPathInfoDetailVO {

    private String name; //地图名称

    private String imgRosPicUrl; //ros地图url

    private String imgOptimizePicUrl;  //优化ros地图url

    private List<MapPointVO> mapPointList;   //点集合

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgRosPicUrl() {
        return imgRosPicUrl;
    }

    public void setImgRosPicUrl(String imgRosPicUrl) {
        this.imgRosPicUrl = imgRosPicUrl;
    }

    public String getImgOptimizePicUrl() {
        return imgOptimizePicUrl;
    }

    public void setImgOptimizePicUrl(String imgOptimizePicUrl) {
        this.imgOptimizePicUrl = imgOptimizePicUrl;
    }

    public List<MapPointVO> getMapPointList() {
        return mapPointList;
    }

    public void setMapPointList(List<MapPointVO> mapPointList) {
        this.mapPointList = mapPointList;
    }
}
