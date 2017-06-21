package cn.mrobot.bean.notification;

/**
 * Created by Jelynn on 2016/11/15.
 */
public enum NotificationType {

	RESOURCE("source", "资源信息"),
	APK("apk", "应用"),
	MODULE("module", "模块"),
	THEME("theme", "主题"),
	AGENT("agent", "agent"),
	AGENT_PATCH("agent_patch", "agent补丁包"),
	SELF_DESIGN_PAD("self_design_pad", "自研平板"),
	MRC_CONTROL("mrc_control", "多机协同"),
	METRICS_FREQUENCY("metrics_frequency", "系统指标收集频率"),
	RECEIPT_MODULE("receipt_module", "打印小票模板");

	private String value;

	private String caption;

	public String getValue() {
		return value;
	}

	public String getCaption() {
		return caption;
	}

	public static String getValue(String caption) {
		String value = "";
		for(NotificationType noticeType : NotificationType.values()){
			if(null != caption && caption.equals(noticeType.getCaption())){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	private NotificationType(String caption, String value) {
		this.caption = caption;
		this.value = value;
	}
}
