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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateCollectorAutoCharge that = (StateCollectorAutoCharge) o;

        return pluginStatus == that.pluginStatus;
    }

    @Override
    public int hashCode() {
        return pluginStatus;
    }
}
