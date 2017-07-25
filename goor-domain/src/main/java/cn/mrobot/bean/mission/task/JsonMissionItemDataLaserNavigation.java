package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataLaserNavigation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double x;
    private Double y;
    private Double th;
    private String map;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getTh() {
        return th;
    }

    public void setTh(Double th) {
        this.th = th;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
