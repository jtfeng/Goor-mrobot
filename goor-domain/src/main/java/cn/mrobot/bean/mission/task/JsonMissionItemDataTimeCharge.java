package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by chay on 2017/8/8.
 */
public class JsonMissionItemDataTimeCharge implements Serializable {

    private static final long serialVersionUID = 123L;

    private JsonMissionItemDataLaserNavigation point;
    private Long chargeTime;

    public JsonMissionItemDataLaserNavigation getPoint() {
        return point;
    }

    public void setPoint(JsonMissionItemDataLaserNavigation point) {
        this.point = point;
    }

    public Long getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(Long chargeTime) {
        this.chargeTime = chargeTime;
    }
}
