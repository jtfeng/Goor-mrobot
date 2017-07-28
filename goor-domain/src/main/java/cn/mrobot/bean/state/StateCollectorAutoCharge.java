package cn.mrobot.bean.state;

/**
 * 自动回充状态
 * Created by Jelynn on 2017/7/17.
 */
public class StateCollectorAutoCharge extends StateCollector {

    private int pluginStatus;

    public int getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(int pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

}
