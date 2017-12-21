package cn.mrobot.bean.erp.appliance;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.constant.Constant;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

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

    private Long packageTypeId;

    private int deleteFlag;   //数据库删除状态 0 :正常 1：删除

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;   //数据库删除时间

    @Transient
    private ApplianceDepartmentType departmentType;

    @Transient
    private AppliancePackageType packageType;

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

    public Long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(Long packageTypeId) {
        this.packageTypeId = packageTypeId;
    }

    public ApplianceDepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(ApplianceDepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    public AppliancePackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(AppliancePackageType packageType) {
        this.packageType = packageType;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public void init(){
        this.setCreateTime(new Date());
        this.setStoreId(100L);
        this.setDeleteFlag(Constant.NORMAL);
    }
}
