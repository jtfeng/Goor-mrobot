package cn.mrobot.bean.erp.password;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Jelynn
 * @date 2017/12/7
 */
@Table(name = "ERP_STATION_PAD_PASSWORD_XREF")
public class OperationPadPasswordXREF extends BaseBean{

    private String password;

    private String mac;

    private Long stationId;

    private int type;  //1:无菌器械室  2：手术室

    @Transient
    private Station station;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
