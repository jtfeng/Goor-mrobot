package cn.muye.erp.appliance.service;

import cn.mrobot.bean.erp.appliance.DepartmentType;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/5
 */
public interface DepartmentTypeService{

    DepartmentType findByName(String name);

    DepartmentType findByCode(int code);

    List<DepartmentType> listAll();

    void save(DepartmentType departmentType);
}
