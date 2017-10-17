package cn.mrobot.bean.area.point;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;

/**
 * Created by Chay on 2017/6/7.
 * 导航目标点
 */
@Table(name = "A_MAP_POINT")
public class MapPoint extends BaseBean {

    //id
    @Transient
    private int value;
    //name
    @Transient
    private String label;

    /**
     * 点名：唯一标识符
     */
	@JSONField(name = "point_name")
	@Column(name = "POINT_NAME")
    private String pointName;
    /**
     * 点别名：显示名称
     */
	@JSONField(name = "point_alias")
	@Column(name = "POINT_ALIAS")
    private String pointAlias;
    /**
     * 地图场景名
     */
	@JSONField(name = "scene_name")
	@Column(name = "SCENE_NAME")
    private String sceneName;
    /**
     * 地图名
     */
	@JSONField(name = "map_name")
	@Column(name = "MAP_NAME")
    private String mapName;
    /**
     * 坐标x
     */
	@JSONField(name = "x")
	@Column(name = "X")
    private double x;
    /**
     * 坐标y
     */
	@JSONField(name = "y")
	@Column(name = "Y")
    private double y;
    /**
     * 坐标旋转角度
     */
	@JSONField(name = "th")
	@Column(name = "TH")
    private double th;

    /**
     * 工控点类型索引
     * */
	@JSONField(name = "point_type")
	@Column(name = "MAP_POINT_TYPE_ID")
    private int mapPointTypeId;

    /**
     * 点类型名称展示
     * */
    @JSONField(name = "ic_point_type")
    @Column(name = "IC_POINT_TYPE")
    private String ICPointType;

    /**
     * 云端点类型索引
     * */
    @JSONField(name = "cloud_point_type")
    @Column(name = "CLOUD_POINT_TYPE_ID")
    private int cloudMapPointTypeId;

	@Column(name = "POINT_LEVEL")
	@JSONField(name = "point_level")

	private int pointLevel;

    private Long mapZipId;

    private int deleteFlag;

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getPointAlias() {
        return pointAlias;
    }

    public void setPointAlias(String pointAlias) {
        this.pointAlias = pointAlias;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getTh() {
        return th;
    }

    public void setTh(double th) {
        this.th = th;
    }

    public int getMapPointTypeId() {
        return mapPointTypeId;
    }

    public void setMapPointTypeId(int mapPointTypeId) {
        this.mapPointTypeId = mapPointTypeId;
    }

	public int getPointLevel() {
		return pointLevel;
	}

	public void setPointLevel(int pointLevel) {
		this.pointLevel = pointLevel;
	}

    public int getValue() {
        if(null == id)
            return 0;
        return  new Long(id).intValue();
    }

    public void setValue(int value) {
        this.value = new Long(id).intValue();
    }

    public String getLabel() {
        return pointAlias;
    }

    public void setLabel(String label) {
        this.label = pointAlias;
    }

    /*@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapPoint mapPoint = (MapPoint) o;

		if (mapPoint == null ||
                mapPoint.getPointName() == null ||
                mapPoint.getSceneName() == null ||
                mapPoint.getMapName() == null){
		    return false;
        }
		return mapPoint.getPointName().equals(pointName)
                && mapPoint.getSceneName().equals(sceneName)
                && mapPoint.getMapName().equals(mapName);

	}*/

	@Override
	public int hashCode() {
		int result = pointName != null ? pointName.hashCode() : 0;
		result = 31 * result + (sceneName != null ? sceneName.hashCode() : 0);
		return result;
	}

    public int getCloudMapPointTypeId() {
        return cloudMapPointTypeId;
    }

    public void setCloudMapPointTypeId(int cloudMapPointTypeId) {
        this.cloudMapPointTypeId = cloudMapPointTypeId;
    }

    public String getICPointType() {
        return ICPointType;
    }

    public void setICPointType(String ICPointType) {
        this.ICPointType = ICPointType;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Long getMapZipId() {
        return mapZipId;
    }

    public void setMapZipId(Long mapZipId) {
        this.mapZipId = mapZipId;
    }

    public static MapPoint copyValue(MapPoint newP,MapPoint mapPoint) {
        newP.setId(mapPoint.getId());
        newP.setCloudMapPointTypeId(mapPoint.getCloudMapPointTypeId());
        newP.setDeleteFlag(mapPoint.getDeleteFlag());
        newP.setICPointType(mapPoint.getICPointType());
        newP.setLabel(mapPoint.getLabel());
        newP.setMapName(mapPoint.getMapName());
        newP.setMapPointTypeId(mapPoint.getMapPointTypeId());
        newP.setMapZipId(mapPoint.getMapZipId());
        newP.setPointAlias(mapPoint.getPointAlias());
        newP.setPointLevel(mapPoint.getPointLevel());
        newP.setPointName(mapPoint.getPointName());
        newP.setSceneName(mapPoint.getSceneName());
        newP.setStoreId(mapPoint.getStoreId());
        newP.setTh(mapPoint.getTh());
        newP.setX(mapPoint.getX());
        newP.setY(mapPoint.getY());
        newP.setCreateTime(mapPoint.getCreateTime());
        newP.setCreatedBy(mapPoint.getCreatedBy());
        return newP;
    }
}
