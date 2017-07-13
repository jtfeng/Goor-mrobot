package cn.mrobot.bean.assets.robot;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ray.Fu on 2017/7/13.
 */
@Table(name = "AS_ROBOT_CHARGER_MAP_POINT_XREF")
public class RobotChargerMapPointXREF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long robotId;

    private Long chargerMapPointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public Long getChargerMapPointId() {
        return chargerMapPointId;
    }

    public void setChargerMapPointId(Long chargerMapPointId) {
        this.chargerMapPointId = chargerMapPointId;
    }
}
