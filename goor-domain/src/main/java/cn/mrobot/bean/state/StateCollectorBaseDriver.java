package cn.mrobot.bean.state;

/**
 * 驱动器状态
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorBaseDriver extends StateCollector{

    private int driverFlow;//驱动器过流

    private int driverError;//编码器错误

    private int poorPosition;//位置超差

    private int driverOverload;//驱动器过载

    private int motorHighTemperature; //电机过温

    private int motorCommunicationBreak; //电机通信断线

    private int PWMControllBreak; //PWM控制断线

    public int getDriverFlow() {
        return driverFlow;
    }

    public void setDriverFlow(int driverFlow) {
        this.driverFlow = driverFlow;
    }

    public int getDriverError() {
        return driverError;
    }

    public void setDriverError(int driverError) {
        this.driverError = driverError;
    }

    public int getPoorPosition() {
        return poorPosition;
    }

    public void setPoorPosition(int poorPosition) {
        this.poorPosition = poorPosition;
    }

    public int getDriverOverload() {
        return driverOverload;
    }

    public void setDriverOverload(int driverOverload) {
        this.driverOverload = driverOverload;
    }

    public int getMotorHighTemperature() {
        return motorHighTemperature;
    }

    public void setMotorHighTemperature(int motorHighTemperature) {
        this.motorHighTemperature = motorHighTemperature;
    }

    public int getMotorCommunicationBreak() {
        return motorCommunicationBreak;
    }

    public void setMotorCommunicationBreak(int motorCommunicationBreak) {
        this.motorCommunicationBreak = motorCommunicationBreak;
    }

    public int getPWMControllBreak() {
        return PWMControllBreak;
    }

    public void setPWMControllBreak(int PWMControllBreak) {
        this.PWMControllBreak = PWMControllBreak;
    }

}
