package cn.muye.erp.operation.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
@RestController
public class OperationDepartmentTypeController {

    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OperationDepartmentTypeController.class);

    @Autowired
    private OperationDepartmentTypeService operationDepartmentTypeService;

    @Autowired
    private OperationTypeService operationTypeService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = "operation/departmentType", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationDepartmentType operationDepartmentType) {
        String name = operationDepartmentType.getName();
        if (StringUtil.isBlank(name)) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_SSKSLBMCBNWK"));
        }
        OperationDepartmentType operationDepartmentTypeDB = operationDepartmentTypeService.findByName(name);
        if (null != operationDepartmentTypeDB) {
            return AjaxResult.failed("(" + name + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_GSSKSLBMCYJCZ"));
        }
        operationDepartmentType.init();
        operationDepartmentTypeService.save(operationDepartmentType);
        return AjaxResult.success(operationDepartmentType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_TJCG"));
    }

    @RequestMapping(value = "operation/departmentType", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody OperationDepartmentType operationDepartmentType) {
        Long id = operationDepartmentType.getId();
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_IDBNWK"));
        }
        OperationDepartmentType operationDepartmentTypeDB = operationDepartmentTypeService.findTypeById(id);
        if (null == operationDepartmentTypeDB) {
            return AjaxResult.failed("(" + operationDepartmentType.getName() + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_GSSKSLBBCZ"));
        }
        OperationDepartmentType departmentType = operationDepartmentTypeService.findByName(operationDepartmentType.getName());
        if (null != departmentType && !id.equals(departmentType.getId())) {
            return AjaxResult.failed("(" + departmentType.getName() + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_GSSKSLBMCCZ"));
        }
        operationDepartmentTypeService.updateSelective(operationDepartmentType);
        return AjaxResult.success(operationDepartmentType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_XGCG"));
    }

    @RequestMapping(value = "operation/departmentType/{id}", method = RequestMethod.GET)
    public AjaxResult findById(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_IDBNWK"));
        }
        OperationDepartmentType operationDepartmentType = operationDepartmentTypeService.findTypeById(id);
        return AjaxResult.success(operationDepartmentType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_CXCG"));
    }

    @RequestMapping(value = "operation/departmentType/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_IDBNWK"));
        }
        //根据手术科室查询关联的手术类型，如果科室关联了手术类型,那么不能删除（提示用户已关联到器械，无法删除）；
        // 如果该手术科室没有关联到任何手术类型，那么改手术科室可以修改和删除（需提示弹框提示）
        LOGGER.info("删除手术科室,id=" + id);
        List<OperationType> operationTypeList = operationTypeService.listByDepartmentTypeId(id);
        OperationDepartmentType departmentType = operationDepartmentTypeService.findTypeById(id);
        if (null != operationTypeList && operationTypeList.size() > 0) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_SSKS") + departmentType.getName() + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_YJGLSSLXWFSC"));
        }
        operationDepartmentTypeService.removeById(id);
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_SCCG"));
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

        return AjaxResult.success(page, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationDepartmentTypeController_java_CXCG"));
    }
}
