package cn.mrobot.bean.erp.appliance;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@Table(name = "ERP_APPLIANCE")
public class Appliance extends BaseBean{

    //器械名称
    private String name;

    //器械名称拼音首字母字符串
    private String searchName;

    //科室类别 （一级分类）
    private int departmentTypeCode;

    private int packageTypeCode;

    @Transient
    private DepartmentType departmentType;

    @Transient
    private PackageType packageType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public int getDepartmentTypeCode() {
        return departmentTypeCode;
    }

    public void setDepartmentTypeCode(int departmentTypeCode) {
        this.departmentTypeCode = departmentTypeCode;
    }

    public int getPackageTypeCode() {
        return packageTypeCode;
    }

    public void setPackageTypeCode(int packageTypeCode) {
        this.packageTypeCode = packageTypeCode;
    }

    public DepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(DepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }
}
