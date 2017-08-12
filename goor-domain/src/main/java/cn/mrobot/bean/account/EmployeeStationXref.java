package cn.mrobot.bean.account;

import javax.persistence.Table;

/**
 * Created by admin on 2017/8/11.
 */
@Table(name = "AC_EMPLOYEE_STATION_XREF")
public class EmployeeStationXref {

    private Long employeeId;

    private Long stationId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
}
