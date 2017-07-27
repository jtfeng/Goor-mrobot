package cn.mrobot.bean.area.station;

import cn.mrobot.bean.area.point.MapPoint;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Created by chay on 2017/6/7.
 * 护士站
 */
@Table(name = "A_STATION_MAP_POINT_XREF")
public class StationMapPointXREF {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    /**
     * 站索引
     */
    private Long stationId;
    /**
     * 点索引
     * */
    private Long mapPointId;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getMapPointId() {
        return mapPointId;
    }

    public void setMapPointId(Long mapPointId) {
        this.mapPointId = mapPointId;
    }

}
