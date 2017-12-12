package cn.mrobot.bean.area.station;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

/**
 * Created by chay on 2017/6/7.
 * 护士站
 */
@Table(name = "A_STATION")
public class Station extends BaseBean{
    /**
     * 站名
     */
    private String name;

    /**
     * 站类型索引
     * */
    private Integer stationTypeId;

    /**
     * 关联场景
     * */
    private Long sceneId;

    /**
     * 假删除标志：0 未删除，1 已删除
     */
    private int active;

    /**
     * 站类型JSON串:不存库
     * */
//    @Transient
//    private String stationType;
    /**
     * 描述
     */
    private String description;

    /**
     * 资源场景
     */
    private String resscene;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Station() {
    }

    public Station(Long id) {
        super(id);
    }

    /**
     * 关联点列表：卸货点、停车点、充电桩点、充电桩原点、装货点等
     * */
    @Transient
    private List<MapPoint> mapPoints;

    /**
     * 前端传到后台的点序列列表
     * key是点类型
     * list是点Id列表
     */
    @Transient
    Map<String,List<MapPoint>> typePoints;

    @Transient
    private List<Robot> robotList;

    @Transient
    private List<Station> accessArriveStationIdList;

    /**
     * 排序索引，作为可到达站时使用
     */
    @Transient
    private Integer orderIndex;

    //是否绑定平板。目前手术室下单系统使用
    @Transient
    private boolean bindingState;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStationTypeId() {
        return stationTypeId;
    }

    public void setStationTypeId(Integer stationTypeId) {
        this.stationTypeId = stationTypeId;
    }

//    public String getStationType() {
//        return stationType;
//    }
//
//    public void setStationType(String stationType) {
//        this.stationType = stationType;
//    }

    public List<MapPoint> getMapPoints() {
        return mapPoints;
    }

    public void setMapPoints(List<MapPoint> mapPoints) {
        this.mapPoints = mapPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Robot> getRobotList() {
        return robotList;
    }

    public void setRobotList(List<Robot> robotList) {
        this.robotList = robotList;
    }

    public List<Station> getAccessArriveStationIdList() {
        return accessArriveStationIdList;
    }

    public void setAccessArriveStationIdList(List<Station> accessArriveStationIdList) {
        this.accessArriveStationIdList = accessArriveStationIdList;
    }

    public Map<String, List<MapPoint>> getTypePoints() {
        return typePoints;
    }

    public void setTypePoints(Map<String, List<MapPoint>> typePoints) {
        this.typePoints = typePoints;
    }

    public String getResscene() {
        return resscene;
    }

    public void setResscene(String resscene) {
        this.resscene = resscene;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isBindingState() {
        return bindingState;
    }

    public void setBindingState(boolean bindingState) {
        this.bindingState = bindingState;
    }
}
