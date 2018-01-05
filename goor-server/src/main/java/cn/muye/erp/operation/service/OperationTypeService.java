package cn.muye.erp.operation.service;

import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseCrudService;

import java.io.File;
import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationTypeService extends BaseCrudService<OperationType> {

    OperationType findOperationTypeById(Long id);

    List<OperationType> findByNameAndDepartmentType(String name, Long departmentTypeId);

    int saveOperationType(OperationType operationType);

    /**
     * 假删除，将deleteFlag置为1
     * @param id
     */
    void removeById(Long id);

    void updateOperationType(OperationType operationType);

    List<OperationType> listAllOperationType(WhereRequest whereRequest);

    boolean importExcel(File uploadFile);

    List<OperationType> listBySearchName(String searchName);

    List<OperationType> listByDepartmentTypeId(Long id);
}
