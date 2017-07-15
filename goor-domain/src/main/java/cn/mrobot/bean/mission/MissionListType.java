package cn.mrobot.bean.mission;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chay on 2017/6/7.
 * 任务列表类型
 */
public enum MissionListType {

	NORMAL("normal", "普通任务"),
	PLAN("plan", "计划任务");

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
		for(MissionListType noticeType : MissionListType.values()){
			if(caption.equals(noticeType.getCaption())){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static MissionListType getType(String caption){
		for (MissionListType c : MissionListType.values()) {
			if (c.getCaption().equals(caption)) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (MissionListType c : MissionListType.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(String caption){
		for (MissionListType c : MissionListType.values()) {
			if (c.getCaption().equals(caption)) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(MissionListType c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		return result;
	}

	private MissionListType(String caption, String value) {
		this.caption = caption;
		this.value = value;
	}

}
