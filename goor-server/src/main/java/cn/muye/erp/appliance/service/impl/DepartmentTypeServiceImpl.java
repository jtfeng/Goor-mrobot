package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.erp.appliance.DepartmentType;
import cn.muye.erp.appliance.mapper.DepartmentTypeMapper;
import cn.muye.erp.appliance.service.DepartmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
@Service
public class DepartmentTypeServiceImpl implements DepartmentTypeService {

    @Autowired
    private DepartmentTypeMapper departmentTypeMapper;


    @Override
    public DepartmentType findByName(String name) {
        return departmentTypeMapper.findByName(name);
    }

    @Override
    public DepartmentType findByCode(int code) {
        return departmentTypeMapper.findByCode(code);
    }

    @Override
    public List<DepartmentType> listAll() {
        return departmentTypeMapper.listAll();
    }

    @Override
    public void save(DepartmentType departmentType) {
        departmentTypeMapper.save(departmentType);
    }
}
