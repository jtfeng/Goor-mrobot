package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataGotocharge implements Serializable {

    private static final long serialVersionUID = 1L;

    private String x;
    private String y;
    private String th;
    private String map;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
