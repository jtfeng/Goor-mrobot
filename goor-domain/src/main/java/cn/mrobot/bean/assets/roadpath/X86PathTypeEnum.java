package cn.mrobot.bean.assets.roadpath;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Chay on 2018/3/29.
 * 任务列表类型
 * //0 表示终点保持原样工控路径
 * //10 代表终点无朝向、坐标范围0.5米要求工控路径
 * //11 代表终点无朝向、坐标范围更小工控路径
 */
public enum X86PathTypeEnum {

	STRICT_DIRECTION(0, "goor_domain_src_main_java_cn_mrobot_bean_roadpath_X86PathTypeEnum_java_JQCXHZB"),
	NO_DIRECTION_500MM_COORDINATE(10, "goor_domain_src_main_java_cn_mrobot_bean_roadpath_X86PathTypeEnum_java_WCX500MMZB"),
	NO_DIRECTION_STICK_COORDINATE(11, "goor_domain_src_main_java_cn_mrobot_bean_roadpath_X86PathTypeEnum_java_WCXJQZB");

	private int caption;

	private String value;

	public String getValue() {
		return value;
	}

	public int getCaption() {
		return caption;
	}

	public static String getValue(String caption) {
		String value = "";
		for(X86PathTypeEnum noticeType : X86PathTypeEnum.values()){
			if(caption.equals(noticeType.getCaption())){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static X86PathTypeEnum getType(int caption){
		for (X86PathTypeEnum c : X86PathTypeEnum.values()) {
			if (c.getCaption()== caption) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (X86PathTypeEnum c : X86PathTypeEnum.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(int caption){
		for (X86PathTypeEnum c : X86PathTypeEnum.values()) {
			if (c.getCaption() == caption) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(X86PathTypeEnum c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		return result;
	}

	X86PathTypeEnum(int caption, String value) {
		this.caption = caption;
		this.value = value;
	}

}
