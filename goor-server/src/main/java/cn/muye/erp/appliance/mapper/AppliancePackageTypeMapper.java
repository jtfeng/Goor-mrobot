package cn.muye.erp.appliance.mapper;

import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
public interface AppliancePackageTypeMapper extends MyMapper<AppliancePackageType>{

    AppliancePackageType findTypeById(int id);

    List<AppliancePackageType> listAll();
}
