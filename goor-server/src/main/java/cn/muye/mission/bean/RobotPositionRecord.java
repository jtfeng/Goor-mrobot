package cn.muye.mission.bean;

import cn.mrobot.bean.area.point.MapPoint;

import java.util.Date;

/**
 * Created by Selim on 2018/1/12.
 */
public class RobotPositionRecord {

    private Date recordDate;  //添加时间

    private MapPoint recordPosition;  //位置点

    public RobotPositionRecord() {
    }

    public RobotPositionRecord(MapPoint recordPosition) {
        this.recordPosition = recordPosition;
        this.recordDate = new Date();
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public MapPoint getRecordPosition() {
        return recordPosition;
    }

    public void setRecordPosition(MapPoint recordPosition) {
        this.recordPosition = recordPosition;
    }
}
