package cn.mrobot.bean.area.station;

import javax.persistence.Table;

/**
 * Created by admin on 2017/8/24.
 */
@Table(name = "A_STATION_STATION_XREF")
public class StationStationXREF {

    private Long originStationId;

    private Long destinationStationId;

    /**
     * 目标站排序索引，这样可以控制目标站的排序
     */
    private Integer orderIndex;

    public Long getOriginStationId() {
        return originStationId;
    }

    public void setOriginStationId(Long originStationId) {
        this.originStationId = originStationId;
    }

    public Long getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(Long destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
