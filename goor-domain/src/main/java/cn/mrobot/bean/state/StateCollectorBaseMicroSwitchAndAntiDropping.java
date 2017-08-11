package cn.mrobot.bean.state;

/**
 * 防碰撞(碰撞开关)传感器,与防跌落传感器状态
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorBaseMicroSwitchAndAntiDropping  extends StateCollector{

    private int leftAntiDropping;//防跌落左传感器

    private int middleAntiDropping;//防跌落中传感器

    private int rightAntiDropping;//防跌落右传感器

    private int leftBaseMicroSwitch; //防碰撞左开关

    private int middleBaseMicroSwitch; //防碰撞中开关

    private int rightBaseMicroSwitch; //防碰撞右开关

    public int getLeftBaseMicroSwitch() {
        return leftBaseMicroSwitch;
    }

    public void setLeftBaseMicroSwitch(int leftBaseMicroSwitch) {
        this.leftBaseMicroSwitch = leftBaseMicroSwitch;
    }

    public int getMiddleBaseMicroSwitch() {
        return middleBaseMicroSwitch;
    }

    public void setMiddleBaseMicroSwitch(int middleBaseMicroSwitch) {
        this.middleBaseMicroSwitch = middleBaseMicroSwitch;
    }

    public int getRightBaseMicroSwitch() {
        return rightBaseMicroSwitch;
    }

    public void setRightBaseMicroSwitch(int rightBaseMicroSwitch) {
        this.rightBaseMicroSwitch = rightBaseMicroSwitch;
    }

    public int getLeftAntiDropping() {
        return leftAntiDropping;
    }

    public void setLeftAntiDropping(int leftAntiDropping) {
        this.leftAntiDropping = leftAntiDropping;
    }

    public int getMiddleAntiDropping() {
        return middleAntiDropping;
    }

    public void setMiddleAntiDropping(int middleAntiDropping) {
        this.middleAntiDropping = middleAntiDropping;
    }

    public int getRightAntiDropping() {
        return rightAntiDropping;
    }

    public void setRightAntiDropping(int rightAntiDropping) {
        this.rightAntiDropping = rightAntiDropping;
    }

}
