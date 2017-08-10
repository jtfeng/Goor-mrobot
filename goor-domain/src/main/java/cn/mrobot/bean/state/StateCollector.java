package cn.mrobot.bean.state;

import cn.mrobot.bean.base.BaseBean;

import java.io.Serializable;

/**
 * Created by Jelynn on 2017/7/27.
 */
public abstract class StateCollector extends BaseBean implements Serializable{

    private boolean databaseFlag; //是否需要存库  true：需要  false：不需要

    private String state; //获取到的状态数据

    public boolean isDatabaseFlag() {
        return databaseFlag;
    }

    public void setDatabaseFlag(boolean databaseFlag) {
        this.databaseFlag = databaseFlag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
