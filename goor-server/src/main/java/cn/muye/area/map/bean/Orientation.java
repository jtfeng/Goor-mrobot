package cn.muye.area.map.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Created by chay on 2017/12/13.
 *
 * ros系统机器人坐标的四元数,满足:w^2+x^2+y^2+z^2=1
 * 用于换算出欧拉角
 */
public class Orientation {

    /**
     * 坐标x
     */
    @JSONField(name = "x")
    private double x;
    /**
     * 坐标y
     */
    @JSONField(name = "y")
    private double y;
    /**
     * 坐标z
     */
    @JSONField(name = "x")
    private double z;
    /**
     * w
     */
    @JSONField(name = "w")
    private double w;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
