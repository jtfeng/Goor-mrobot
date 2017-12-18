package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.appliance.mapper.AppliancePackageTypeMapper;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
@Service
public class AppliancePackageTypeServiceImpl extends BaseServiceImpl<AppliancePackageType> implements AppliancePackageTypeService {

    @Autowired
    private AppliancePackageTypeMapper appliancePackageTypeMapper;


    @Override
    public AppliancePackageType findByName(String name) {
        Example example = new Example(AppliancePackageType.class);
        example.createCriteria().andCondition("NAME='" + name + "'");
        List<AppliancePackageType> appliancePackageTypes = appliancePackageTypeMapper.selectByExample(example);
        if (appliancePackageTypes.size() > 0){
            return appliancePackageTypes.get(0);
        }
        return null;
    }

    @Override
    public AppliancePackageType findTypeById(int id) {
        return appliancePackageTypeMapper.findTypeById(id);
    }

    @Override
    public List<AppliancePackageType> listAll() {
        return appliancePackageTypeMapper.listAll();
    }
}
