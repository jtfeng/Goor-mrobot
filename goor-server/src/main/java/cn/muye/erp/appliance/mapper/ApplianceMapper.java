package cn.muye.erp.appliance.mapper;

import cn.mrobot.bean.erp.appliance.Appliance;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
public interface ApplianceMapper extends MyMapper<Appliance> {

    Appliance findApplianceById(Long id);


    List<Appliance> listApplianceByCondition(@Param("name") String name,
                                             @Param("searchName") String searchName,
                                             @Param("departmentType") String departmentType,
                                             @Param("packageType") String packageType,
                                             @Param("storeId") Long storeId);

    List<Appliance> listAllAppliance(@Param("storeId") Long storeId);

    List<Appliance> listBySearchName(@Param("searchName") String searchName);

    List<Appliance> listByPackageTypeId(@Param("packageTypeId") Long packageTypeId);
}
