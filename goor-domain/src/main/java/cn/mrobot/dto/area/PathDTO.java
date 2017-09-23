package cn.mrobot.dto.area;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Jelynn on 2017/9/18.
 */
public class PathDTO {

    //  "id" : 0,  固定路径id
    @JSONField(name = "id")
    private long id;

    // "start_id":1,  id对应点名称，具体查看artemis
    @JSONField(name = "start_id")
    private String startId;

    //"start_map":"4",
    @JSONField(name = "start_map")
    private String startMap;

    //"start_th":2.443952333429646,
    @JSONField(name = "start_th")
    private double startTh;

    // "start_x":0.09492947242027938,
    @JSONField(name = "start_x")
    private double startX;

    //  "start_y":0.2713897015097970,
    @JSONField(name = "start_y")
    private double startY;

    // "end_id" : 2,  id对应点名称，具体查看artemis
    @JSONField(name = "end_id")
    private String endId;

    //"end_map" : "4",
    @JSONField(name = "end_map")
    private String endMap;

    //"end_th" : 1.297045166020602,
    @JSONField(name = "end_th")
    private double endTh;

    // "end_x" : -0.4847978322562696,
    @JSONField(name = "end_x")
    private double endX;

    //"end_y" : 2.006885179782557,
    @JSONField(name = "end_y")
    private double endY;

    //  "valid":1
    @JSONField(name = "valid")
    private int valid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getStartMap() {
        return startMap;
    }

    public void setStartMap(String startMap) {
        this.startMap = startMap;
    }

    public double getStartTh() {
        return startTh;
    }

    public void setStartTh(double startTh) {
        this.startTh = startTh;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public String getEndMap() {
        return endMap;
    }

    public void setEndMap(String endMap) {
        this.endMap = endMap;
    }

    public double getEndTh() {
        return endTh;
    }

    public void setEndTh(double endTh) {
        this.endTh = endTh;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }
}
