package cn.mrobot.bean.account;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by ray.fu on 2017/8/11.
 */
@Table(name = "AC_EMPLOYEE")
public class Employee extends BaseBean{

    private String name; //员工名称

    private String code; //员工编号

    private String description; //备注

//    private Boolean activated = Boolean.TRUE;; //激活

    private Integer type; //员工类型(1. 普通， 2. 电梯管理员)

    @Transient
    private List<Station> stationList; //站点List

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Station> getStationList() {
        return stationList;
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
    }

//    public Boolean getActivated() {
//        return activated;
//    }
//
//    public void setActivated(Boolean activated) {
//        this.activated = activated;
//    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

