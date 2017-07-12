package cn.mrobot.bean.area.point.cascade;

import cn.mrobot.bean.area.point.MapPoint;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 15:46
 * Describe:
 * Version:1.0
 */
public class CascadeMapPointType {

	private int id;

	private String name;

	private List<MapPoint> children;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MapPoint> getChildren() {
		return children;
	}

	public void setChildren(List<MapPoint> children) {
		this.children = children;
	}
}
