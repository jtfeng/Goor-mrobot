package cn.mrobot.bean.area.station;

import javax.persistence.Table;

/**
 * Created by admin on 2017/8/24.
 */
@Table(name = "A_STATION_STATION_XREF")
public class StationStationXREF {

    private Long originStationId;

    private Long destinationStationId;

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
}
