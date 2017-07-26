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
public enum MissionListTypeEnum {

	NORMAL("normal", "普通任务","normal"),
	PLAN("plan", "计划任务","plan"),
	PATROL("patrol", "巡逻任务","plan"),
	CHARGE("charge", "充电任务","plan");

	private String caption;

	private String value;

	private String dtoCaption;//传到任务管理器的类型

	public String getValue() {
		return value;
	}

	public String getCaption() {
		return caption;
	}

	public String getDtoCaption() {
		return dtoCaption;
	}

	public static String getValue(String caption) {
		String value = "";
		for(MissionListTypeEnum noticeType : MissionListTypeEnum.values()){
			if(caption.equals(noticeType.getCaption())){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	//获取传到任务管理器的类型
	public static String getDtoCaption(String caption) {
		String dtoCaption = "";
		for(MissionListTypeEnum noticeType : MissionListTypeEnum.values()){
			if(caption.equals(noticeType.getCaption())){
				dtoCaption = noticeType.getDtoCaption();
			}
		}
		return dtoCaption;
	}

	public static MissionListTypeEnum getType(String caption){
		for (MissionListTypeEnum c : MissionListTypeEnum.values()) {
			if (c.getCaption().equals(caption)) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (MissionListTypeEnum c : MissionListTypeEnum.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(String caption){
		for (MissionListTypeEnum c : MissionListTypeEnum.values()) {
			if (c.getCaption().equals(caption)) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(MissionListTypeEnum c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		result.put("dtoCaption",c.getDtoCaption());
		return result;
	}

	private MissionListTypeEnum(String caption, String value,String dtoCaption) {
		this.caption = caption;
		this.value = value;
		this.dtoCaption = dtoCaption;
	}

}
