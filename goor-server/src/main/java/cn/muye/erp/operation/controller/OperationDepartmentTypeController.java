package cn.muye.erp.operation.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
@RestController
public class OperationDepartmentTypeController {

    @Autowired
    private OperationDepartmentTypeService operationDepartmentTypeService;

    @RequestMapping(value = "operation/departmentType", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationDepartmentType operationDepartmentType) {
        String name = operationDepartmentType.getName();
        if (StringUtil.isBlank(name)) {
            return AjaxResult.failed("手术科室类别名称不能为空");
        }
        OperationDepartmentType operationDepartmentTypeDB = operationDepartmentTypeService.findByName(name);
        if (null != operationDepartmentTypeDB) {
            return AjaxResult.failed("(" + name + ") 该手术科室类别名称已经存在");
        }
        operationDepartmentType.init();
        operationDepartmentTypeService.save(operationDepartmentType);
        return AjaxResult.success(operationDepartmentType, "添加成功");
    }

    @RequestMapping(value = "operation/departmentType", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody OperationDepartmentType operationDepartmentType) {
        Long id = operationDepartmentType.getId();
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        OperationDepartmentType operationDepartmentTypeDB = operationDepartmentTypeService.findTypeById(id);
        if (null == operationDepartmentTypeDB) {
            return AjaxResult.failed("(" + operationDepartmentType.getName() + ")该手术科室类别不存在");
        }
        OperationDepartmentType departmentType = operationDepartmentTypeService.findByName(operationDepartmentType.getName());
        if (null != departmentType && !id.equals(departmentType.getId())) {
            return AjaxResult.failed("(" + departmentType.getName() + ") 该手术科室类别名称存在");
        }
        operationDepartmentTypeService.updateSelective(operationDepartmentType);
        return AjaxResult.success(operationDepartmentType, "修改成功");
    }

    @RequestMapping(value = "operation/departmentType/{id}", method = RequestMethod.GET)
    public AjaxResult findById(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        OperationDepartmentType operationDepartmentType = operationDepartmentTypeService.findTypeById(id);
        return AjaxResult.success(operationDepartmentType, "查询成功");
    }

    @RequestMapping(value = "operation/departmentType/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        operationDepartmentTypeService.removeById(id);
        return AjaxResult.success("删除成功");
    }

    @RequestMapping(value = "operation/departmentType", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装
        List<OperationDepartmentType> appliancePackageTypeList = operationDepartmentTypeService.listAllType(whereRequest);
        PageInfo<OperationDepartmentType> page = new PageInfo<OperationDepartmentType>(appliancePackageTypeList);

        return AjaxResult.success(page, "查询成功");
    }
}
