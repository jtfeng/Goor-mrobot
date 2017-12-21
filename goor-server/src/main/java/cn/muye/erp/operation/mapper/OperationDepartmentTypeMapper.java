package cn.muye.erp.operation.mapper;

import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationDepartmentTypeMapper extends MyMapper<OperationDepartmentType>{

    /**
     * OperationTypeMapper.xml中使用
     * @param id
     * @return
     */
    OperationDepartmentType findDepartmentTypeById(@Param("id") Long id);
}
