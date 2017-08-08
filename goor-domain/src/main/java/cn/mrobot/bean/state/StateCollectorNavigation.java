package cn.mrobot.bean.state;

/**
 * 导航状态
 * Created by Jelynn on 2017/7/18.
 */
public class StateCollectorNavigation extends StateCollector {

    private int navigationTypeCode;

    public int getNavigationTypeCode() {
        return navigationTypeCode;
    }

    public void setNavigationTypeCode(int navigationTypeCode) {
        this.navigationTypeCode = navigationTypeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateCollectorNavigation that = (StateCollectorNavigation) o;

        return navigationTypeCode == that.navigationTypeCode;
    }

    @Override
    public int hashCode() {
        return navigationTypeCode;
    }
}
