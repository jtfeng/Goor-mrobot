package cn.muye.log.base;


import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.muye.log.base.service.LogInfoService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 9:47
 * Describe:日志保存的工具类
 * Version:1.0
 */
@Service
public class LogInfoUtils implements ApplicationContextAware {

	public static ApplicationContext applicationContext;

//	public static class Builder {
//
//		private String deviceId;
//		private String message;
//		private LogType logType;
//		private LogLevel logLevel;
//		private Date createDate = new Date();
//
//		public Builder() {
//		}
//
//		public Builder deviceId(String deviceId) {
//			this.deviceId = deviceId;
//			return this;
//		}
//
//		public Builder message(String message) {
//			this.message = message;
//			return this;
//		}
//
//		public Builder logType(LogType logType) {
//			this.logType = logType;
//			return this;
//		}
//
//		public Builder logLevel(LogLevel logLevel) {
//			this.logLevel = logLevel;
//			return this;
//		}
//
//		public Builder createDate(Date createDate) {
//			this.createDate = createDate;
//			return this;
//		}
//
//		public LogInfoUtils build() {
//			return new LogInfoUtils(this);
//		}
//	}
//
//	public LogInfoUtils(Builder builder) {
//
//	}

	public static void info(String deviceId, LogType logType, String message) {

		log(LogLevel.INFO, deviceId, logType, message);
	}

	public static void warn(String deviceId, LogType logType, String message) {
		log(LogLevel.WARNING, deviceId, logType, message);
	}

	public static void error(String deviceId, LogType logType, String message) {
		log(LogLevel.ERROR, deviceId, logType, message);
	}

	private static void log(LogLevel level, String deviceId, LogType logType, String message) {
		LogInfo logInfo = new LogInfo();
		logInfo.setDeviceId(deviceId);
		logInfo.setMessage(message);
		logInfo.setLogTypeName(logType.getName());
		logInfo.setLogLevelName(level.getName());
		logInfo.setCreateDate(new Date());
		LogInfoService logInfoService = applicationContext.getBean(LogInfoService.class);
		logInfoService.save(logInfo);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		LogInfoUtils.applicationContext = applicationContext;
	}
}
