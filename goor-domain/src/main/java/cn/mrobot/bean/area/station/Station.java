package cn.mrobot.bean.area.station;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by chay on 2017/6/7.
 * 护士站
 */
@Table(name = "A_STATION")
public class Station extends BaseBean{
    /**
     * 站名
     */
    private String name;

    /**
     * 站类型索引
     * */
    private Integer stationTypeId;

    /**
     * 站类型JSON串:不存库
     * */
//    @Transient
//    private String stationType;
    /**
     * 描述
     */
    private String description;
    /**
     * 关联点列表：卸货点、停车点、充电桩点、充电桩原点、装货点等
     * */
    @Transient
    private List<MapPoint> mapPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStationTypeId() {
        return stationTypeId;
    }

    public void setStationTypeId(Integer stationTypeId) {
        this.stationTypeId = stationTypeId;
    }

//    public String getStationType() {
//        return stationType;
//    }
//
//    public void setStationType(String stationType) {
//        this.stationType = stationType;
//    }

    public List<MapPoint> getMapPoints() {
        return mapPoints;
    }

    public void setMapPoints(List<MapPoint> mapPoints) {
        this.mapPoints = mapPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
