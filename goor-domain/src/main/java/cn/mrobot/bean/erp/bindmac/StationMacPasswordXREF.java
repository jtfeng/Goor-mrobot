package cn.mrobot.bean.erp.bindmac;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 *  手术室 ：  电脑MAC和手术室的绑定关系
 * @author Jelynn
 * @date 2017/12/7
 */
@Table(name = "ERP_STATION_MAC_PASSWORD_XREF")
public class StationMacPasswordXREF extends BaseBean{

    private String password;

    private String mac;

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


    public enum  Type{
        ASEPTIC_APPARATUS_ROOM(1),
        OPERATION(2);

        private int code;

        private Type(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public void init(){
        this.setCreateTime(new Date());
        this.setStoreId(100L);
    }
}
