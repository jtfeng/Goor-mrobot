package cn.muye.erp.appliance.mapper;

import cn.mrobot.bean.erp.appliance.ApplianceDepartmentType;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/5
 */
public interface ApplianceDepartmentTypeMapper {

    ApplianceDepartmentType findByName(String name);

    ApplianceDepartmentType findByCode(int code);

    List<ApplianceDepartmentType> listAll();

    void save(ApplianceDepartmentType departmentType);

    Integer findMaxCode();
}
