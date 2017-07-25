package cn.mrobot.bean.state;

/**
 *
 * 底盘系统状态
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorBaseSystem {

    private int res;

    private int powerOn;//开机

    private int normal;//正常

    private int IOEmergencyStop;//IO急停

    private int switchEmergencyStop; //开关急停

    private int underVoltageEmergencyStop; //欠压停机

    private int overSpeedEmergencyStop; //过速停机

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getPowerOn() {
        return powerOn;
    }

    public void setPowerOn(int powerOn) {
        this.powerOn = powerOn;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getIOEmergencyStop() {
        return IOEmergencyStop;
    }

    public void setIOEmergencyStop(int IOEmergencyStop) {
        this.IOEmergencyStop = IOEmergencyStop;
    }

    public int getSwitchEmergencyStop() {
        return switchEmergencyStop;
    }

    public void setSwitchEmergencyStop(int switchEmergencyStop) {
        this.switchEmergencyStop = switchEmergencyStop;
    }

    public int getUnderVoltageEmergencyStop() {
        return underVoltageEmergencyStop;
    }

    public void setUnderVoltageEmergencyStop(int underVoltageEmergencyStop) {
        this.underVoltageEmergencyStop = underVoltageEmergencyStop;
    }

    public int getOverSpeedEmergencyStop() {
        return overSpeedEmergencyStop;
    }

    public void setOverSpeedEmergencyStop(int overSpeedEmergencyStop) {
        this.overSpeedEmergencyStop = overSpeedEmergencyStop;
    }
}
