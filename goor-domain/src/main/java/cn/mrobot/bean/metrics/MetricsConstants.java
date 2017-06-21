package cn.mrobot.bean.metrics;

/**
 * Created with IntelliJ IDEA.
 * Project Name : mpusher-client-java
 * User: Jelynn
 * Date: 2017/5/31
 * Time: 13:50
 * Describe:
 * Version:1.0
 */
public class MetricsConstants {

	public static final int METRICS_PERIOD = 5;  //数据收集周期(s)
	//monitor start
	public static final String SLASH = "/";//分隔符或根目录

	public static final String NODE_ROOT_MONITOR = "monitor";//指标的顶级树目录

	public static final String NODE_SUMMARY = "summary";//系统和应用不常更改的指标

	public static final String NODE_METRICS = "metrics";//系统或应用经常变化的指标

	public static final String NODE_APP_INFO = "appInfo";//应用信息

	public static final String NODE_METRICS_INFO = "metricsInfo";//详细指标信息

	public static final String PATH_METRICS = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_METRICS + SLASH;

	public static final String PATH_SUMMARY = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_SUMMARY  + SLASH;

	public static final String PATH_METRICS_APPINFO = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_METRICS + SLASH + NODE_APP_INFO + SLASH;

	public static final String PATH_SUMMARY_APPINFO = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_SUMMARY + SLASH + NODE_APP_INFO + SLASH;

	public static final String PATH_METRICS_METRICSINFO = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_METRICS + SLASH + NODE_METRICS_INFO + SLASH;

	public static final String PATH_SUMMARY_METRICSINFO = SLASH + NODE_ROOT_MONITOR + SLASH + NODE_SUMMARY + SLASH + NODE_METRICS_INFO + SLASH;

//	public static final String NODE_DEVICE_MEMORY = "deviceMemory";//设备内存
//
//	public static final String NAME_DEVICE_MEMORY = "设备内存";//设备内存
//	public static final String NAME_DEVICE_CPU = "设备CPU";//设备内存

	public static final String METRIC_productLocalIP = "productLocalIP";

	public static final String METRIC_charging = "charging";  //是否充电  0充电,1未充电

	public static final String METRIC_ramUsed = "ramUsed";

	public static final String METRIC_romUsed = "romUsed";

	public static final String METRIC_volume = "volume";

	public static final String METRIC_wifiStatus = "wifiStatus";

	public static final String METRIC_SSID = "SSID";

	public static final String METRIC_wifiRssi = "wifiRssi";  //wifi信号强度

	public static final String METRIC_bluetoothState = "bluetoothState";

	public static final String METRIC_productPower = "productPower";

	public static final String METRIC_comment = "comment";

	public static final String METRIC_cpuUsed = "cpuUsed";

	public static final String SUMMARY_productGlobalIP = "productGlobalIP";

	public static final String SUMMARY_cpuName = "cpuName";

	public static final String SUMMARY_maxFrequency = "maxFrequency";

	public static final String SUMMARY_minFrequency = "minFrequency";

	public static final String SUMMARY_computerType = "computerType";

	public static final String SUMMARY_macAddress = "macAddress";

	public static final String SUMMARY_brand = "brand";

	public static final String SUMMARY_manufacturer = "manufacturer";  //制造商

	public static final String SUMMARY_deviceMode = "deviceModel";

	public static final String SUMMARY_serial = "serial";

	public static final String SUMMARY_resolution = "resolution";  //分辨率

	public static final String SUMMARY_language = "language";

	public static final String SUMMARY_osVersion = "osVersion";

	public static final String SUMMARY_osKernelVersion = "osKernelVersion";

	public static final String SUMMARY_osUUID = "osUUID";

	public static final String SUMMARY_ramTotal = "ramTotal";

	public static final String SUMMARY_romTotal = "romTotal";
}
