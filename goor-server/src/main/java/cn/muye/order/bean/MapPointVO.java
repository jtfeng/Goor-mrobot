package cn.muye.order.bean;

/**
 * Created by Selim on 2017/11/29.
 * 坐标显示
 */
public class MapPointVO {

    private Double x;

    private Double y;

    private Double th;

    private String name; //坐标关联的位置名称

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getTh() {
        return th;
    }

    public void setTh(Double th) {
        this.th = th;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
