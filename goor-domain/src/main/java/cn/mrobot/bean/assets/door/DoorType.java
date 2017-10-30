package cn.mrobot.bean.assets.door;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chay on 2017/6/7.
 * 站类型
 */
public enum DoorType {

	PATH_DOOR("pathDoor", "固定路径门");

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
		for(DoorType noticeType : DoorType.values()){
			if(caption.equals(noticeType.getCaption())){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static DoorType getType(int caption){
		for (DoorType c : DoorType.values()) {
			if (c.getCaption().equals(caption)) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (DoorType c : DoorType.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(String caption){
		for (DoorType c : DoorType.values()) {
			if (c.getCaption().equals(caption)) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(DoorType c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		return result;
	}

	DoorType(String caption, String value) {
		this.caption = caption;
		this.value = value;
	}

}
