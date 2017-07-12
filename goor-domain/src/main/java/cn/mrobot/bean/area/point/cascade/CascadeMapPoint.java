package cn.mrobot.bean.area.point.cascade;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 15:45
 * Describe:
 * Version:1.0
 */
public class CascadeMapPoint {

	private int id;

	private String mapName;

	private List<CascadeMapPointType> children;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public List<CascadeMapPointType> getChildren() {
		return children;
	}

	public void setChildren(List<CascadeMapPointType> children) {
		this.children = children;
	}
}
