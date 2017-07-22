package cn.mrobot.bean.state;

import cn.mrobot.bean.base.BaseBean;

/**
 * 自动回充状态
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorAutoCharge extends BaseBean {

    private int pluginStatus;

    public int getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(int pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

}
