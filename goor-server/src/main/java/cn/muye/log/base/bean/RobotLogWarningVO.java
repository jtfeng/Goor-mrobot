package cn.muye.log.base.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/11/22.
 */
public class RobotLogWarningVO {

    @JSONField(format = "yyyy/MM/dd")
    private Date date;

    private int warningTime;

    private List<RobotLogWarningDetail> warningDetails;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public List<RobotLogWarningDetail> getWarningDetails() {
        return warningDetails;
    }

    public void setWarningDetails(List<RobotLogWarningDetail> warningDetails) {
        this.warningDetails = warningDetails;
    }


}
