package cn.muye.erp.operation.mapper;

import cn.mrobot.bean.erp.operation.OperationType;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationTypeMapper extends MyMapper<OperationType> {

    OperationType findOperationTypeById(@Param("id") Long id);

    int saveOperationType(OperationType operationType);

    void updateOperationType(OperationType operationType);

    List<OperationType> listOperationTypeBy(@Param("name") String name,
                                            @Param("searchName") String searchName,
                                            @Param("operationDepartmentTypeId") Long operationDepartmentType);

    List<OperationType> listAllOperationType();

    List<OperationType> listBySearchName(@Param("searchName") String searchName);

    List<OperationType> listByDepartmentTypeId(@Param("departmentTypeId") Long DepartmentTypeId);
}
