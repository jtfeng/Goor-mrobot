package cn.mrobot.bean.assets.door;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by chay on 2017/8/16.
 * 自动门
 */
@Table(name = "AS_DOOR")
public class Door  extends BaseBean {
    private String name;
    private String lockState = "0";// 0表示 未锁定、1表示 已锁定
    private Long waitPoint;//等门点，加锁任务
    private Long goPoint;//进门点,执行开门任务
    private Long outPoint;//出门点，解锁任务
    private String ip;
    private String info;
    private String robotCode;//被哪个机器人锁住
    private Long sceneId;//所属云端场景ID
    private String sceneName;//地图场景名
    private String mapName;//地图名
    private int active;//假删除标志：0 未删除，1 已删除
    private String doorOrderType;//门对应的任务类型：比如普通导航door，沿线导航laneDoor，固定路径导航pathDoor
    private String pathId;//工控路径ID

    @Transient
    private MapPoint wPoint;//等门点，加锁任务
    @Transient
    private MapPoint gPoint;//进门点,执行开门任务
    @Transient
    private MapPoint oPoint;//出门点，解锁任务

    @Override
    public String toString() {
        return "Door{" +
                "name='" + name + '\'' +
                ", lockState='" + lockState + '\'' +
                ", waitPoint=" + waitPoint +
                ", goPoint=" + goPoint +
                ", id=" + id +
                ", outPoint=" + outPoint +
                ", storeId=" + storeId +
                ", createdBy=" + createdBy +
                ", ip='" + ip + '\'' +
                ", info='" + info + '\'' +
                ", robotCode='" + robotCode + '\'' +
                ", createTime=" + createTime +
                ", sceneId=" + sceneId +
                ", sceneName='" + sceneName + '\'' +
                ", mapName='" + mapName + '\'' +
                ", active=" + active +
                ", doorOrderType='" + doorOrderType + '\'' +
                ", pathId='" + pathId + '\'' +
                ", wPoint=" + wPoint +
                ", gPoint=" + gPoint +
                ", oPoint=" + oPoint +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public Long getWaitPoint() {
        return waitPoint;
    }

    public void setWaitPoint(Long waitPoint) {
        this.waitPoint = waitPoint;
    }

    public Long getGoPoint() {
        return goPoint;
    }

    public void setGoPoint(Long goPoint) {
        this.goPoint = goPoint;
    }

    public Long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(Long outPoint) {
        this.outPoint = outPoint;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public MapPoint getwPoint() {
        return wPoint;
    }

    public void setwPoint(MapPoint wPoint) {
        this.wPoint = wPoint;
    }

    public MapPoint getgPoint() {
        return gPoint;
    }

    public void setgPoint(MapPoint gPoint) {
        this.gPoint = gPoint;
    }

    public MapPoint getoPoint() {
        return oPoint;
    }

    public void setoPoint(MapPoint oPoint) {
        this.oPoint = oPoint;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDoorOrderType() {
        return doorOrderType;
    }

    public void setDoorOrderType(String doorOrderType) {
        this.doorOrderType = doorOrderType;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }
}
