package cn.muye.area.map.bean;

/**
 * @author Created by chay on 2017/12/13.
 * ros系统返回的坐标值封装类
 */
public class RosCurrentPose {
    /**
     * ros系统四元数，用于计算欧拉角
     */
    Orientation orientation;

    /**
     * ros系统坐标
     */
    Position position;

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
