package cn.muye.erp.operation.service;

import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseCrudService;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationDepartmentTypeService extends BaseCrudService<OperationDepartmentType>{

    OperationDepartmentType findTypeById(Long id);

    List<OperationDepartmentType> listAllType();

    List<OperationDepartmentType> listAllType(WhereRequest whereRequest);
    /**
     * 假删除，置DeleteFlag为0
     * @param id
     * @return
     */
    int removeById(Long id);

    OperationDepartmentType findByName(String name);
}
