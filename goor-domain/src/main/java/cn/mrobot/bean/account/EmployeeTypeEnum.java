package cn.mrobot.bean.account;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chay on 2017/6/7.
 * 员工类型
 */
public enum EmployeeTypeEnum {

	NORMAL(0, "goor_domain_src_main_java_cn_mrobot_bean_account_EmployeeTypeEnum_java_PTYG"),
	ELEVATOR(1, "goor_domain_src_main_java_cn_mrobot_bean_account_EmployeeTypeEnum_java_DTYG");

	private String value;

	private int caption;

	public String getValue() {
		return value;
	}

	public int getCaption() {
		return caption;
	}

	public static String getValue(int caption) {
		String value = "";
		for(EmployeeTypeEnum noticeType : EmployeeTypeEnum.values()){
			if(caption==noticeType.getCaption()){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static EmployeeTypeEnum getType(int caption){
		for (EmployeeTypeEnum c : EmployeeTypeEnum.values()) {
			if (c.getCaption() == caption) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (EmployeeTypeEnum c : EmployeeTypeEnum.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(int caption){
		for (EmployeeTypeEnum c : EmployeeTypeEnum.values()) {
			if (c.getCaption() == caption) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(EmployeeTypeEnum c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		return result;
	}

	private EmployeeTypeEnum(int caption, String value) {
		this.caption = caption;
		this.value = value;
	}

}
