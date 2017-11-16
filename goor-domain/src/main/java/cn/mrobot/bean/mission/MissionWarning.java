package cn.mrobot.bean.mission;

import cn.mrobot.bean.area.station.Station;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Selim on 2017/10/23.
 */
@Table(name = "MISSION_WARNING")
public class MissionWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startStationId; //起始点id

    private Long endStationId;   //结束点id

    private Long artificalTime;  //人为定义超时时间

    private Long calculateTime;  //计算定义时间

    private Boolean useArtificalTime = Boolean.FALSE; //是否启用人为定义时间

    private Integer actionTime;  //执行次数

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;    //创建时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;     //更新时间

    @Transient
    private Station startStation; //起始站详情

    @Transient
    private Station endStation; //结束站详情

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(Long startStationId) {
        this.startStationId = startStationId;
    }

    public Long getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(Long endStationId) {
        this.endStationId = endStationId;
    }

    public Long getArtificalTime() {
        return artificalTime;
    }

    public void setArtificalTime(Long artificalTime) {
        this.artificalTime = artificalTime;
    }

    public Long getCalculateTime() {
        return calculateTime;
    }

    public void setCalculateTime(Long calculateTime) {
        this.calculateTime = calculateTime;
    }

    public Boolean getUseArtificalTime() {
        return useArtificalTime;
    }

    public void setUseArtificalTime(Boolean useArtificalTime) {
        this.useArtificalTime = useArtificalTime;
    }

    public Integer getActionTime() {
        return actionTime;
    }

    public void setActionTime(Integer actionTime) {
        this.actionTime = actionTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }
}
