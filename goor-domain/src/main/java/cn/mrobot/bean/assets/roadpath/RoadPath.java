package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

@Table(name = "AS_ROADPATH")
public class RoadPath extends BaseBean{

    // 路径名称信息
    private String pathName;
    // 拟合方式
    private String pattern;
    // 路径绑定的数据
    private String data;
    // 云端场景 id 信息
    private Long cloudSceneId;
    // 开始点
    private Long startPoint;
    // 结束点
    private Long endPoint;
    // 权值大小
    private Long weight;

    public RoadPath(){}

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getCloudSceneId() {
        return cloudSceneId;
    }

    public void setCloudSceneId(Long cloudSceneId) {
        this.cloudSceneId = cloudSceneId;
    }

    public Long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Long startPoint) {
        this.startPoint = startPoint;
    }

    public Long getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Long endPoint) {
        this.endPoint = endPoint;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "RoadPath{" +
                "pathName='" + pathName + '\'' +
                ", pattern='" + pattern + '\'' +
                ", data='" + data + '\'' +
                ", cloudSceneId=" + cloudSceneId +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", weight=" + weight +
                '}';
    }
}