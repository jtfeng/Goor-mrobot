package cn.mrobot.bean.log;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Project Name : mpusher-client-java
 * User: Jelynn
 * Date: 2017/6/2
 * Time: 14:51
 * Describe:
 * Version:1.0
 */
public class ExecutorLog implements Serializable {

	private ExecutorLogType type;//日志类型

	private String data;//日志数据

	public ExecutorLog() {
	}

	public ExecutorLog(ExecutorLogType type, String data) {
		this.type = type;
		this.data = data;
	}

	public ExecutorLogType getType() {
		return type;
	}

	public void setType(ExecutorLogType type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
