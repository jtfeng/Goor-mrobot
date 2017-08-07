package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "AS_ELEVATORSHAFT")
public class ElevatorShaft extends BaseBean {

    private String name;
    private String info;
    @Transient
    private List<Elevator> elevators;

    @Override
    public String toString() {
        return "ElevatorShaft{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", elevators=" + elevators +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void setElevators(List<Elevator> elevators) {
        this.elevators = elevators;
    }
}