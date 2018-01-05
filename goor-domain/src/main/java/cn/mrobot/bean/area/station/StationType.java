package cn.mrobot.bean.area.station;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chay on 2017/6/7.
 * 站类型
 */
public enum StationType {

	CENTER(1, "中心站点"),
	NORMAL(2, "一般站点"),
	CHARGE(3, "充电站点"),
	OPERATION(4, "手术室站点"),
	ASEPTIC_APPARATUS_ROOM(5, "无菌器械室站点"),
	ELEVATOR(6, "电梯站点");

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
		for(StationType noticeType : StationType.values()){
			if(caption==noticeType.getCaption()){
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static StationType getType(int caption){
		for (StationType c : StationType.values()) {
			if (c.getCaption() == caption) {
				return c;
			}
		}
		return null;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (StationType c : StationType.values()) {
			resultList.add(toDTO(c)) ;
		}
		return resultList;
	}

	public static String getTypeJson(int caption){
		for (StationType c : StationType.values()) {
			if (c.getCaption() == caption) {
				return JSON.toJSONString(toDTO(c));
			}
		}
		return null;
	}

	private static Map toDTO(StationType c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		return result;
	}

	private StationType(int caption, String value) {
		this.caption = caption;
		this.value = value;
	}

}
