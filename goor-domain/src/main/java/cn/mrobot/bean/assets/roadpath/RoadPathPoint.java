package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "AS_ROADPATHPOINT")
public class RoadPathPoint extends BaseBean{

    // 当前点 id 信息
    private Long pointId;
    // 下一个点 id 信息
    private Long nextPointId;
    // 是否为开始点的标记（1：是、0：否）
    private Integer startFlag;
    // 是否为结束点的标记（1：是、0：否）
    private Integer endFlag;
    // 对应绑定的路径 id 编号
    private Long roadPathId;

    public RoadPathPoint(){}

    public RoadPathPoint(Long pointId, Long nextPointId, Integer startFlag, Integer endFlag, Long roadPathId) {
        this.pointId = pointId;
        this.nextPointId = nextPointId;
        this.startFlag = startFlag;
        this.endFlag = endFlag;
        this.roadPathId = roadPathId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Long getNextPointId() {
        return nextPointId;
    }

    public void setNextPointId(Long nextPointId) {
        this.nextPointId = nextPointId;
    }

    public Integer getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(Integer startFlag) {
        this.startFlag = startFlag;
    }

    public Integer getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Integer endFlag) {
        this.endFlag = endFlag;
    }

    public Long getRoadPathId() {
        return roadPathId;
    }

    public void setRoadPathId(Long roadPathId) {
        this.roadPathId = roadPathId;
    }

    @Override
    public String toString() {
        return "RoadPathPoint{" +
                "pointId=" + pointId +
                ", nextPointId=" + nextPointId +
                ", startFlag=" + startFlag +
                ", endFlag=" + endFlag +
                ", roadPathId=" + roadPathId +
                '}';
    }
}