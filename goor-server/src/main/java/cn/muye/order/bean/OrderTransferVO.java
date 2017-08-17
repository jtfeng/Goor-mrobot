package cn.muye.order.bean;

/**
 * Created by Selim on 2017/8/15.
 */
public class OrderTransferVO {

    private String stationName;

    private String finishDate;

    private Integer status; //状态

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
