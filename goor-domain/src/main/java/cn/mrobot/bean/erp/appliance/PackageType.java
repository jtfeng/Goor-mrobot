package cn.mrobot.bean.erp.appliance;

import javax.persistence.Table;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@Table(name = "ERP_PACKAGE_TYPE")
public class PackageType{

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
