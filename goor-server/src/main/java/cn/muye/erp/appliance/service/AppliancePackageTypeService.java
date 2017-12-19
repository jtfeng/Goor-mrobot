package cn.muye.erp.appliance.service;

import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/5
 */
public interface AppliancePackageTypeService extends BaseService<AppliancePackageType> {

    AppliancePackageType findByName(String name);

    AppliancePackageType findTypeById(Long id);

    List<AppliancePackageType> listAllPackageType();
    /**
     * 假删除，置DeleteFlag为0
     * @param id
     * @return
     */
    int removeById(Long id);

    List<AppliancePackageType> listAllPackageType(WhereRequest whereRequest);
}
