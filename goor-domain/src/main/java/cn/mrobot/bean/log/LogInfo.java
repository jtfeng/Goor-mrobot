package cn.mrobot.bean.log;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 9:37
 * Describe:
 * Version:1.0
 */
@Table(name = "LOG_INFO")
public class LogInfo extends BaseBean {

	/**
	 * 设备编号
	 */
	private String deviceId;

	/**
	 * 日志等级
	 */
	private String logLevel;

	/**
	 * 日志类型
	 */
	private String logType;

	/**
	 * 地图
	 */
	private String mapName;

	/**
	 * 机器人场景
	 */
	private String sceneName;

	private String baseState; //底盘状态

	private String chargeState; //充电状态

	private String missionState; //任务状态

	private String navigationState; //导航状态

	private String handlePerson;  //警告错误处理人

	private String handleTime; //警告错误处理时间

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
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

	public String getBaseState() {
		return baseState;
	}

	public void setBaseState(String baseState) {
		this.baseState = baseState;
	}

	public String getChargeState() {
		return chargeState;
	}

	public void setChargeState(String chargeState) {
		this.chargeState = chargeState;
	}

	public String getMissionState() {
		return missionState;
	}

	public void setMissionState(String missionState) {
		this.missionState = missionState;
	}

	public String getHandlePerson() {
		return handlePerson;
	}

	public void setHandlePerson(String handlePerson) {
		this.handlePerson = handlePerson;
	}

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}

	public String getNavigationState() {
		return navigationState;
	}

	public void setNavigationState(String navigationState) {
		this.navigationState = navigationState;
	}
}
