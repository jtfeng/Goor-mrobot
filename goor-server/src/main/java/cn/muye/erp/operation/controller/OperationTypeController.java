package cn.muye.erp.operation.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.operation.OperationDefaultApplianceXREF;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.erp.operation.mapper.OperationDefaultApplianceXREFMapper;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
@RestController
public class OperationTypeController {

    private static Logger LOGGER = LoggerFactory.getLogger(OperationTypeController.class);

    @Autowired
    private OperationTypeService operationTypeService;

    @Autowired
    private OperationDefaultApplianceXREFMapper operationDefaultApplianceXREFMapper;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @RequestMapping(value = "operation/type", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationType operationType) {
        AjaxResult validateResult = validateOperationType(operationType);
        if (!validateResult.isSuccess()){
            return validateResult;
        }
        if (StringUtil.isBlank(operationType.getSearchName())) {
            operationType.setSearchName(StringUtil.getSearchName(operationType.getName()));
        }
        operationType.init();
        operationTypeService.saveOperationType(operationType);
        Long operationTypeId = operationType.getId();
        //保存手术类型的默认器械
        saveOperationDefaultApplianceXREF(operationTypeId, operationType.getApplianceList());
        return AjaxResult.success(operationType,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_TJCG"));
    }

    @RequestMapping(value = "operation/type", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody OperationType operationType) {
        AjaxResult validateResult = validateOperationType(operationType);
        if (!validateResult.isSuccess()){
            return validateResult;
        }

        operationTypeService.updateOperationType(operationType);
        //更新手术类型的默认器械
        List<OperationDefaultApplianceXREF> applianceXREFList = operationType.getApplianceList();
        updateOperationDefaultApplianceXREF(operationType.getId(), applianceXREFList);
        return AjaxResult.success(operationType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_XGCG"));
    }

    public AjaxResult validateOperationType(OperationType operationType){
        String name = operationType.getName();
        OperationDepartmentType departmentType = operationType.getOperationDepartmentType();
        if (StringUtil.isBlank(name) || null == departmentType) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_SSMCHZSSKSBNWK"));
        }
        List<OperationType> operationTypeList = operationTypeService.findByNameAndDepartmentType(name, departmentType.getId());
        if (null != operationTypeList && operationTypeList.size() > 0 && !operationTypeList.get(0).getId().equals(operationType.getId())) {
            return AjaxResult.failed("(" + name + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_SSLXMCYJCZ"));
        }
        return AjaxResult.success();
    }

    @RequestMapping(value = "operation/type/{id}", method = RequestMethod.GET)
    public AjaxResult findById(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_IDBNWK"));
        }
        OperationType operationType = operationTypeService.findOperationTypeById(id);
        return AjaxResult.success(operationType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_CXCG"));
    }

    @RequestMapping(value = "operation/type/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_IDBNWK"));
        }
        operationTypeService.removeById(id);
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_SCCG"));
    }

    @RequestMapping(value = "operation/type", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装
        List<OperationType> operationTypeList = operationTypeService.listAllOperationType(whereRequest);
        PageInfo<OperationType> page = new PageInfo<OperationType>(operationTypeList);

        return AjaxResult.success(page, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_CXCG"));
    }

    /**
     * 数据的导入模板下载
     *
     * @return
     */
    @RequestMapping(value = "operation/type/import/templateDownload", method = RequestMethod.GET)
    public AjaxResult templateDownload() {
        String url = DOWNLOAD_HTTP + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_100TEMPLATESSSMCDRSJMBXLSX");
        LOGGER.info("额外器械数据的导入模板下载地址= " + url);
        return AjaxResult.success(url, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_CZCG"));
    }

    /**
     * 根据用户输入的字母和数字，查询出匹配结果排序规则按照全匹配，头匹配，包含匹配排序，每一种匹配规则又按照createTime倒序
     * @param searchName
     * @return
     */
    @RequestMapping(value = "services/operation/type/searchName", method = RequestMethod.GET)
    public AjaxResult listBySearchName(@RequestParam("searchName") String searchName) {
        if (StringUtil.isBlank(searchName)) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_QXCXMCBNWK"));
        }
        searchName = searchName.toUpperCase();
        List<OperationType> operationTypeList = operationTypeService.listBySearchName(searchName);
        return AjaxResult.success(operationTypeList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_operation_controller_OperationTypeController_java_CXCG"));
    }

    private void updateOperationDefaultApplianceXREF(Long operationTypeId, List<OperationDefaultApplianceXREF> applianceList) {
        if (null == applianceList) {
            return;
        }
        //删除原关联关系
        operationDefaultApplianceXREFMapper.deleteByOperationId(operationTypeId);
        saveOperationDefaultApplianceXREF(operationTypeId, applianceList);
    }

    private void saveOperationDefaultApplianceXREF(Long operationTypeId, List<OperationDefaultApplianceXREF> applianceList) {
        if (null == applianceList || applianceList.size() <= 0) {
            return;
        }
        for (OperationDefaultApplianceXREF xref : applianceList) {
            xref.setOperationTypeId(operationTypeId);
            operationDefaultApplianceXREFMapper.save(xref);
        }
    }
}
