package cn.muye.erp.appliance.mapper;

import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
public interface AppliancePackageTypeMapper extends MyMapper<AppliancePackageType> {

    /**
     * ApplianceMapper.xml文件有使用
     * @param id
     * @return
     */
    AppliancePackageType findTypeById(@Param("id") int id);

    /**
     * 根据名称查询，模糊匹配
     * @param name
     * @return
     */
    List<AppliancePackageType> listByName(@Param("name") String name);

}
