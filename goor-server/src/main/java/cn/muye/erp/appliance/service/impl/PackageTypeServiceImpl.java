package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.erp.appliance.PackageType;
import cn.muye.erp.appliance.mapper.PackageTypeMapper;
import cn.muye.erp.appliance.service.PackageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
@Service
public class PackageTypeServiceImpl implements PackageTypeService {

    @Autowired
    private PackageTypeMapper packageTypeMapper;


    @Override
    public PackageType findByName(String name) {
        return packageTypeMapper.findByName(name);
    }

    @Override
    public PackageType findByCode(int code) {
        return packageTypeMapper.findByCode(code);
    }

    @Override
    public List<PackageType> listAll() {
        return packageTypeMapper.listAll();
    }

    @Override
    public void save(PackageType packageType) {
        packageTypeMapper.save(packageType);
    }
}
