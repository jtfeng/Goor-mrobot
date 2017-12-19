package cn.muye.erp.appliance.service;

import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.io.File;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
public interface ApplianceService extends BaseService<Appliance> {

    Appliance findApplianceById(Long id);

    boolean importExcel(File file);

    List<Appliance> lists(WhereRequest whereRequest);

    List<Appliance> listBySearchName(String searchName, Long fakeMerchantStoreId);

    List<Appliance> findByNameAndCode(String name, int departmentTypeCode, Long packageTypeId);

    /**
     * 假删除，置DeleteFlag为0
     * @param id
     * @return
     */
    int removeById(Long id);

}
