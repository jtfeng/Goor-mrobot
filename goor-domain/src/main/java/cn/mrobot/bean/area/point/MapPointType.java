package cn.mrobot.bean.area.point;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chay on 2017/6/7.
 * 导航目标点类型
 */
public enum MapPointType {


	/*********
	 * 工控那边约定目标点类型
	 * 0 为初始点,
	 * 1 为充点电,
	 * 2 为普通目标点,后续
	 * 可继续细分目标点类
	 * 型
	 *******/
	UNDEFINED(0, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_WPZYDLX", 0, 0.1,0), //

	PARK(1, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_TCW", 2, 0.1,0), //工控 2普通目标点
	CHARGER_STAND_BY(2, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_CDZYD", 1, 0.1,0),//工控 1充电点
	CHARGER(3, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_CDZ", 2, 0.1,0),//工控 2普通目标点
	ELEVATOR(4, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_CDTD", 2, 0.1,0),//工控 2普通目标点
	ELEVATOR_WAIT(5, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_DDTD", 2, 0.1,0),//工控 2普通目标点
	DOOR(6, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_ZDM", 2, 0.1,6.28),//工控 2普通目标点
	CROSS(7, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_LK", 2, 0.1,6.28),//工控 2普通目标点
	CORNER(8, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_GJ", 2, 0.1,6.28),//工控 2普通目标点
	TRANSITION(9, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_GDD", 2, 0.1,6.28),//工控 2普通目标点
	UNLOAD(10, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_XHD", 2, 0.1,0),//工控 2普通目标点
	LOAD(11, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_ZHD", 2, 0.1,0),//工控 2普通目标点
	INITIAL(12, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_CSD", 0, 0.1,0),//工控 0初始点
	ELEVATOR_START(13, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_KSDTRWD", 2, 0.1,0),//工控 2普通目标点
	ELEVATOR_INNER(14, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_DTNBD", 2, 0.1,0),//工控 2普通目标点
	ELEVATOR_END(15, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_DTRWJSD", 2, 0.1,0),//工控 2普通目标点
	DOOR_WAIT(16, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_MRWDDD", 2, 0.1,0),//工控 2普通目标点
	DOOR_START(17, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_MRWKSD", 2, 0.1,0),//工控 2普通目标点
	DOOR_END(18, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_MRWJSD", 2, 0.1,0),//工控 2普通目标点
	FINAL_UNLOAD(19, "goor_domain_src_main_java_cn_mrobot_bean_area_point_MapPointType_java_HJHSD", 2, 0.1,0),//工控 2普通目标点

	;

	private String value;

	private int caption;

	private int industrialControlCaption;//对应工控那边的地图点类型

	/**
	 * 点范围，根据类型不同，点范围不同
	 */
	private double scale;

	/**
	 * 点方向，导航到目标点对方向的要求。
	 * 0：表示用点自身的方向，由导航控制误差
	 * 2π(约6.28)：表示无方向要求，以任一方向导航到该点，都算导航到了目标点。
	 */
	private double direction;

	public String getValue() {
		return value;
	}

	public int getCaption() {
		return caption;
	}

	public int getIndustrialControlCaption() {
		return industrialControlCaption;
	}

	public double getScale() {
		return scale;
	}

	public double getDirection() {
		return direction;
	}

	public static String getValue(int caption) {
		String value = "";
		for (MapPointType noticeType : MapPointType.values()) {
			if (caption == noticeType.getCaption()) {
				value = noticeType.getValue();
			}
		}
		return value;
	}

	public static MapPointType getType(int caption) {
		for (MapPointType c : MapPointType.values()) {
			if (c.getCaption() == caption) {
				return c;
			}
		}
		return null;
	}

	public static String getTypeJson(int caption){
		for (MapPointType c : MapPointType.values()) {
			if (c.getCaption() == caption) {
				Map result = new HashMap<String,Object>();
				result.put("name",c);
				result.put("value",c.getValue());
				result.put("caption",c.getCaption());
				result.put("industrialControlCaption",c.getIndustrialControlCaption());
				result.put("scale",c.getScale());
				result.put("direction",c.getDirection());
				return JSON.toJSONString(result);
			}
		}
		return null;
	}

	private MapPointType(int caption, String value, int industrialControlCaption, double scale, double direction) {
		this.caption = caption;
		this.value = value;
		this.industrialControlCaption = industrialControlCaption;
		this.scale = scale;
		this.direction = direction;
	}

	public static List list() {
		List<Map> resultList = new ArrayList<Map>();
		for (MapPointType c : MapPointType.values()) {
			resultList.add(mapPointTypeToEntity(c)) ;
		}
		return resultList;
	}

	public static Map mapPointTypeToEntity(MapPointType c) {
		Map result = new HashMap<String,Object>();
		result.put("name",c);
		result.put("value",c.getValue());
		result.put("caption",c.getCaption());
		result.put("industrialControlCaption", c.getIndustrialControlCaption());
		result.put("scale", c.getScale());
		result.put("direction",c.getDirection());
		return result;
	}

	public static List list(List<MapPointType> mapPointTypes) {
		List<Map> resultList = new ArrayList<Map>();
		for (MapPointType c : mapPointTypes) {
			resultList.add(mapPointTypeToEntity(c)) ;
		}
		return resultList;
	}
}
