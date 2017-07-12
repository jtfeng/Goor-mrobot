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

	//id
	private int value;

	//name
	private String label;

	private List<CascadeMapPointType> children;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<CascadeMapPointType> getChildren() {
		return children;
	}

	public void setChildren(List<CascadeMapPointType> children) {
		this.children = children;
	}
}
