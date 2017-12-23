package cn.muye.area.map.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author created by chay on 2017/12/13.
 *
 * ros系统机器人坐标
 */
public class Position {

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
    @JSONField(name = "z")
    private double z;

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
}
