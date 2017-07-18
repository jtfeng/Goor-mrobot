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

	private String overlayMap;

	private Long mapZipId;

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

	public String getOverlayMap() {
		return overlayMap;
	}

	public void setOverlayMap(String overlayMap) {
		this.overlayMap = overlayMap;
	}

	public Long getMapZipId() {
		return mapZipId;
	}

	public void setMapZipId(Long mapZipId) {
		this.mapZipId = mapZipId;
	}
}
