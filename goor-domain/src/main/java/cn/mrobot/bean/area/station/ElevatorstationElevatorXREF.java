package cn.mrobot.bean.area.station;


import javax.persistence.Table;

/**
 * @author wlkfec
 */
@Table(name = "A_ELEVATORSTATION_ELEVATOR_XREF")
public class ElevatorstationElevatorXREF {
    private Long id;
    private Long elevatorstationId;
    private Long elevatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElevatorstationId() {
        return elevatorstationId;
    }

    public void setElevatorstationId(Long elevatorstationId) {
        this.elevatorstationId = elevatorstationId;
    }

    public Long getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(Long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
