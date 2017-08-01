package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.base.BaseBean;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "AS_ELEVATORPOINTCOMBINATION")
public class ElevatorPointCombination extends BaseBean {
    private String name;
    private String info;

    private Long waitPoint;
    private Long goPoint;
    private Long outPoint;
    private Long innerPoint;

    @Transient
    private MapPoint wPoint;
    @Transient
    private MapPoint gPoint;
    @Transient
    private MapPoint oPoint;
    @Transient
    private MapPoint iPoint;
    @Transient
    private List<Elevator> elevators;

    @Override
    public String toString() {
        return "ElevatorPointCombination{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", waitPoint=" + waitPoint +
                ", goPoint=" + goPoint +
                ", outPoint=" + outPoint +
                ", innerPoint=" + innerPoint +
                ", wPoint=" + wPoint +
                ", gPoint=" + gPoint +
                ", oPoint=" + oPoint +
                ", iPoint=" + iPoint +
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

    public Long getWaitPoint() {
        return waitPoint;
    }

    public void setWaitPoint(Long waitPoint) {
        this.waitPoint = waitPoint;
    }

    public Long getGoPoint() {
        return goPoint;
    }

    public void setGoPoint(Long goPoint) {
        this.goPoint = goPoint;
    }

    public Long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(Long outPoint) {
        this.outPoint = outPoint;
    }

    public Long getInnerPoint() {
        return innerPoint;
    }

    public void setInnerPoint(Long innerPoint) {
        this.innerPoint = innerPoint;
    }

    public MapPoint getwPoint() {
        return wPoint;
    }

    public void setwPoint(MapPoint wPoint) {
        this.wPoint = wPoint;
    }

    public MapPoint getgPoint() {
        return gPoint;
    }

    public void setgPoint(MapPoint gPoint) {
        this.gPoint = gPoint;
    }

    public MapPoint getoPoint() {
        return oPoint;
    }

    public void setoPoint(MapPoint oPoint) {
        this.oPoint = oPoint;
    }

    public MapPoint getiPoint() {
        return iPoint;
    }

    public void setiPoint(MapPoint iPoint) {
        this.iPoint = iPoint;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void setElevators(List<Elevator> elevators) {
        this.elevators = elevators;
    }
}