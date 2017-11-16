package cn.mrobot.bean.area.map;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/7/5
 * Time: 14:55
 * Describe:
 * Version:1.0
 */
@Table(name = "A_MAP_INFO")
public class MapInfo extends BaseBean {

	private String mapName;

	private String sceneName;

	private String mapAlias;

	private String pngImageLocalPath;

	@Transient
	private String pngImageHttpPath;

	private String ros;

	private String deviceId;

	private String pngDesigned; //  设计图的相对路径

	@Transient
	private String pngDesignedHttpPath;

	private Long mapZipId;

	private int floor;

	private int logicFloor;//逻辑楼层

	private int deleteFlag;   //0 : 删除   1：未删除

	public int getLogicFloor() {
		return logicFloor;
	}

	public void setLogicFloor(int logicFloor) {
		this.logicFloor = logicFloor;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getMapAlias() {
		return mapAlias;
	}

	public void setMapAlias(String mapAlias) {
		this.mapAlias = mapAlias;
	}

	public String getPngImageLocalPath() {
		return pngImageLocalPath;
	}

	public void setPngImageLocalPath(String pngImageLocalPath) {
		this.pngImageLocalPath = pngImageLocalPath;
	}

	public String getPngImageHttpPath() {
		return pngImageHttpPath;
	}

	public void setPngImageHttpPath(String pngImageHttpPath) {
		this.pngImageHttpPath = pngImageHttpPath;
	}

	public String getRos() {
		return ros;
	}

	public void setRos(String ros) {
		this.ros = ros;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	public Long getMapZipId() {
		return mapZipId;
	}

	public void setMapZipId(Long mapZipId) {
		this.mapZipId = mapZipId;
	}

	public String getPngDesigned() {
		return pngDesigned;
	}

	public void setPngDesigned(String pngDesigned) {
		this.pngDesigned = pngDesigned;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getPngDesignedHttpPath() {
		return pngDesignedHttpPath;
	}

	public void setPngDesignedHttpPath(String pngDesignedHttpPath) {
		this.pngDesignedHttpPath = pngDesignedHttpPath;
	}

	@Override
	public String toString() {
		return "MapInfo{" +
				"mapName='" + mapName + '\'' +
				", sceneName='" + sceneName + '\'' +
				", mapAlias='" + mapAlias + '\'' +
				", pngImageLocalPath='" + pngImageLocalPath + '\'' +
				", pngImageHttpPath='" + pngImageHttpPath + '\'' +
				", ros='" + ros + '\'' +
				", deviceId='" + deviceId + '\'' +
				", pngDesigned='" + pngDesigned + '\'' +
				", pngDesignedHttpPath='" + pngDesignedHttpPath + '\'' +
				", mapZipId=" + mapZipId +
				", floor=" + floor +
				", logicFloor=" + logicFloor +
				", deleteFlag=" + deleteFlag +
				'}';
	}
}
