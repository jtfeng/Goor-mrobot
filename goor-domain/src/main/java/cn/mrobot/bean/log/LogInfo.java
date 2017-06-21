package cn.mrobot.bean.log;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
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
public class LogInfo implements Serializable {

	private long id;

	/**
	 * 设备编号
	 */
	private String deviceId;
	/**
	 * 信息内容
	 */
	private String message;

	/**
	 * 日志类型
	 */
	private LogType logType;

	private String logTypeName;

	/**
	 * 日志等级
	 */
	private LogLevel logLevel;

	private String logLevelName;

	private String handlePerson;  //警告错误处理人

	private String handleTime; //警告错误处理时间

	/**
	 * 日志记录时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogTypeName() {
		return logTypeName;
	}

	public void setLogTypeName(String logTypeName) {
		this.logTypeName = logTypeName;
	}

	public String getLogLevelName() {
		return logLevelName;
	}

	public void setLogLevelName(String logLevelName) {
		this.logLevelName = logLevelName;
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
}
