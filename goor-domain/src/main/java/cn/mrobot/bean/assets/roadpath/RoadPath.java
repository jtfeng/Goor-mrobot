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
    // 开始点
    private Long startPoint;
    // 结束点
    private Long endPoint;
    // 权值大小
    private Long weight;
    //场景名
    private String sceneName;
    //地图名
    private String mapName;
    //路径编号（由于现在包含了工控上传的路径，故额外添加一个字段）
    private String pathId;
    //路径类型（此处暂定为 0 表示云端配置 1 代表工控上传）
    private Integer pathType;
    //所关联的路径锁对象
    private Long pathLock;

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

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public Integer getPathType() {
        return pathType;
    }

    public void setPathType(Integer pathType) {
        this.pathType = pathType;
    }

    public Long getPathLock() {
        return pathLock;
    }

    public void setPathLock(Long pathLock) {
        this.pathLock = pathLock;
    }

    @Override
    public String toString() {
        return "RoadPath{" +
                "pathName='" + pathName + '\'' +
                ", pattern='" + pattern + '\'' +
                ", data='" + data + '\'' +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", weight=" + weight +
                ", sceneName='" + sceneName + '\'' +
                ", mapName='" + mapName + '\'' +
                ", pathId='" + pathId + '\'' +
                ", pathType=" + pathType +
                ", pathLock=" + pathLock +
                '}';
    }
}