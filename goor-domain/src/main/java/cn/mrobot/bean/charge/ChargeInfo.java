package cn.mrobot.bean.charge;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/2
 * Time: 15:02
 * Describe: 充电状态实体
 * Version:1.0
 */
@Table(name = "CHARGE_INFO")
public class ChargeInfo extends BaseBean{

	@JSONField(name = "device_id")
	private String deviceId;

	@JSONField(name = "status")
	private int chargingStatus; //充电状态  1：正在充电  0：未充电

	@JSONField(name = "plugin_status")
	private int pluginStatus; // 1：插入充电桩   0：未插入充电桩

	@JSONField(name = "power_percent")
	private int powerPercent;  //电量  范围  0-100

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getChargingStatus() {
		return chargingStatus;
	}

	public void setChargingStatus(int chargingStatus) {
		this.chargingStatus = chargingStatus;
	}

	public int getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(int pluginStatus) {
		this.pluginStatus = pluginStatus;
	}

	public int getPowerPercent() {
		return powerPercent;
	}

	public void setPowerPercent(int powerPercent) {
		this.powerPercent = powerPercent;
	}

}
