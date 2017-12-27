package cn.mrobot.bean.assets.roadpath;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

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
    //工控路径类型（此处暂定为 0 表示终点保持原样工控路径 10 代表终点无朝向要求工控路径）
    private Integer x86PathType;
    //受管路径的开始限制时间
    private Date restrictedStarttime;
    //受管路径的结束限制时间
    private Date restrictedEndtime;
    //受管路径的开始限制时间(前台接收)
    @Transient
    private Long restrictedStarttimeLongTime;
    //受管路径的结束限制时间(前台接收)
    @Transient
    private Long restrictedEndtimeLongTime;


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

    public Integer getX86PathType() {
        return x86PathType;
    }

    public void setX86PathType(Integer x86PathType) {
        this.x86PathType = x86PathType;
    }

    public Date getRestrictedStarttime() {
        return restrictedStarttime;
    }

    public void setRestrictedStarttime(Date restrictedStarttime) {
        this.restrictedStarttime = restrictedStarttime;
    }

    public Date getRestrictedEndtime() {
        return restrictedEndtime;
    }

    public void setRestrictedEndtime(Date restrictedEndtime) {
        this.restrictedEndtime = restrictedEndtime;
    }

    public Long getRestrictedStarttimeLongTime() {
        return restrictedStarttimeLongTime;
    }

    public void setRestrictedStarttimeLongTime(Long restrictedStarttimeLongTime) {
        this.restrictedStarttimeLongTime = restrictedStarttimeLongTime;
        this.restrictedStarttime = new Date(restrictedStarttimeLongTime);
    }

    public Long getRestrictedEndtimeLongTime() {
        return restrictedEndtimeLongTime;
    }

    public void setRestrictedEndtimeLongTime(Long restrictedEndtimeLongTime) {
        this.restrictedEndtimeLongTime = restrictedEndtimeLongTime;
        this.restrictedEndtime = new Date(restrictedEndtimeLongTime);
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
                ", x86PathType=" + x86PathType +
                '}';
    }
}