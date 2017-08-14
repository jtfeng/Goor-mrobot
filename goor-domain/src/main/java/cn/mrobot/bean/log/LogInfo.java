package cn.mrobot.bean.log;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import java.util.Date;

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

	//模块
	private int module;  //ModuleEnums 枚举类

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

	private String message; //具体信息

	private String handlePerson;  //警告错误处理人

	private Date handleTime; //警告错误处理时间

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

	public String getHandlePerson() {
		return handlePerson;
	}

	public void setHandlePerson(String handlePerson) {
		this.handlePerson = handlePerson;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getModule() {
		return module;
	}

	public void setModule(int module) {
		this.module = module;
	}
}
