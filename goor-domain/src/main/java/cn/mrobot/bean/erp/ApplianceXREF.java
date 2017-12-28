package cn.mrobot.bean.erp;

import cn.mrobot.bean.erp.appliance.Appliance;

/**
 *
 * @author Jelynn
 * @date 2017/12/28
 */
public abstract class ApplianceXREF {

    private Appliance appliance;

    private int number;

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
