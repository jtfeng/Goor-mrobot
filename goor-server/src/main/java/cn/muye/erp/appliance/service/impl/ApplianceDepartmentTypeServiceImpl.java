package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.erp.appliance.ApplianceDepartmentType;
import cn.muye.erp.appliance.mapper.ApplianceDepartmentTypeMapper;
import cn.muye.erp.appliance.service.ApplianceDepartmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
@Service
public class ApplianceDepartmentTypeServiceImpl implements ApplianceDepartmentTypeService {

    @Autowired
    private ApplianceDepartmentTypeMapper applianceDepartmentTypeMapper;

    @Override
    public ApplianceDepartmentType findByName(String name) {
        return applianceDepartmentTypeMapper.findByName(name);
    }

    @Override
    public ApplianceDepartmentType findByCode(int code) {
        return applianceDepartmentTypeMapper.findByCode(code);
    }

    @Override
    public List<ApplianceDepartmentType> listAll() {
        return applianceDepartmentTypeMapper.listAll();
    }

    @Override
    public void save(ApplianceDepartmentType departmentType) {
        applianceDepartmentTypeMapper.save(departmentType);
    }
}
