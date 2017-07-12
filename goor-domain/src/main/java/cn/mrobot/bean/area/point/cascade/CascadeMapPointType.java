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

    //id
    private int value;
    //name
    private String label;

    private List<MapPoint> children;

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

    public List<MapPoint> getChildren() {
        return children;
    }

    public void setChildren(List<MapPoint> children) {
        this.children = children;
    }
}
