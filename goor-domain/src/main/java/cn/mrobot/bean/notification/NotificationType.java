package cn.mrobot.bean.notification;

/**
 * Created by Jelynn on 2016/11/15.
 */
public enum NotificationType {

	RESOURCE("source", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_ZYXX"),
	APK("apk", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_YY"),
	MODULE("module", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_MK"),
	THEME("theme", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_ZT"),
	AGENT("agent", "agent"),
	AGENT_PATCH("agent_patch", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_AGENTBDB"),
	SELF_DESIGN_PAD("self_design_pad", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_ZYPB"),
	MRC_CONTROL("mrc_control", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_DJXT"),
	METRICS_FREQUENCY("metrics_frequency", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_XTZBSJPL"),
	RECEIPT_MODULE("receipt_module", "goor_domain_src_main_java_cn_mrobot_bean_notification_NotificationType_java_DYXPMB");

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
