package cn.mrobot.bean.assets.door;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by chay on 2017/8/16.
 * 自动门
 */
@Table(name = "AS_DOOR")
public class Door  extends BaseBean {

    private String name;
    private String lockState = "0";// 0表示 未锁定、1表示 已锁定
    private Long waitPoint;//等门点，加锁任务
    private Long goPoint;//进门点,执行开门任务
    private Long outPoint;//出门点，解锁任务
    private String ip;
    private String info;
    private String robotCode;//被哪个机器人锁住

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public Long getWaitPoint() {
        return waitPoint;
    }

    public void setWaitPoint(Long waitPoint) {
        this.waitPoint = waitPoint;
    }

    public Long getGoPoint() {
        return goPoint;
    }

    public void setGoPoint(Long goPoint) {
        this.goPoint = goPoint;
    }

    public Long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(Long outPoint) {
        this.outPoint = outPoint;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }
}
