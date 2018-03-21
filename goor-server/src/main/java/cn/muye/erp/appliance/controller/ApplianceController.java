package cn.muye.erp.appliance.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.ApplianceDepartmentType;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.erp.appliance.service.ApplianceDepartmentTypeService;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import cn.muye.erp.appliance.service.ApplianceService;
import cn.muye.erp.appliance.service.impl.ApplianceServiceImpl;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.erp.operation.service.impl.OperationTypeServiceImpl;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@RestController
public class ApplianceController {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplianceController.class);

    @Autowired
    private ApplianceService applianceService;

    @Autowired
    private AppliancePackageTypeService appliancePackageTypeService;

    @Autowired
    private ApplianceDepartmentTypeService applianceDepartmentTypeService;

    @Autowired
    private OperationTypeService operationTypeService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    private static String APPLIANCE_IMPORT_FILE_PATH = "appliance_import_file";
    private static String OPERATION_IMPORT_FILE_PATH = "operation_import_file";
    private static String OPERATION_DEFAULT_APPLIANCE_IMPORT_FILE_PATH = "operation_default_appliance_import_file_path";

    @RequestMapping(value = "appliance", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Appliance appliance) {
        if (null == appliance) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_BCDXBNWK"));
        }
        if (StringUtil.isBlank(appliance.getName()) ||
                appliance.getDepartmentTypeCode() == 0 ||
                appliance.getPackageTypeId() == null) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_BCDXGJSXBNWK"));
        }
        //校验包装类别
        AppliancePackageType appliancePackageType = appliancePackageTypeService.findTypeById(appliance.getPackageTypeId());
        if (null == appliancePackageType) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_BZLBBCZHYSCQYTJ"));
        }
        AjaxResult validateResult = validateAppliance(appliance);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        appliance.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        appliance.setCreateTime(new Date());
        if (StringUtil.isBlank(appliance.getSearchName())) {
            appliance.setSearchName(StringUtil.getSearchName(appliance.getName()));
        }
        appliance.setSearchName(appliance.getSearchName().toUpperCase());
        applianceService.save(appliance);
        return AjaxResult.success(appliance, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_TJCG"));
    }

    @RequestMapping(value = "appliance", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody Appliance appliance) {
        if (null == appliance) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_XGDXBNWK"));
        }
        if (StringUtil.isBlank(appliance.getName())) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_XGDXMCBNWK"));
        }
        if (null == appliance.getId()) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_XGDXIDBNWK"));
        }

        AjaxResult validateResult = validateAppliance(appliance);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        int departmentTypeCode = appliance.getDepartmentTypeCode();
        appliance.setDepartmentType(applianceDepartmentTypeService.findByCode(departmentTypeCode));
        Long packageTypeId = appliance.getPackageTypeId();
        appliance.setPackageType(appliancePackageTypeService.findTypeById(packageTypeId));
        appliance.setCreateTime(new Date());
        applianceService.updateSelective(appliance);
        return AjaxResult.success(appliance, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_XGCG"));
    }

    private AjaxResult validateAppliance(Appliance appliance) {
        //根据name,departmentTypeCode,packageTypeId进行重复校验
        List<Appliance> applianceList = applianceService.findByNameAndCode(appliance.getName(),
                appliance.getDepartmentTypeCode());
        if (null != applianceList && applianceList.size() > 0 && !applianceList.get(0).getId().equals(appliance.getId())) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_XGSBTMCLBBZLXSJYJCZ"));
        }
        return AjaxResult.success();
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.GET)
    public AjaxResult get(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_IDBNWK"));
        }
        Appliance applianceDB = applianceService.findApplianceById(id);
        return AjaxResult.success(applianceDB, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_CXCG"));
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_IDBNWK"));
        }
        applianceService.removeById(id);
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_SCCG"));
    }

    /**
     * 根据用户输入的字母和数字，查询出匹配结果排序规则按照全匹配，头匹配，包含匹配排序，每一种匹配规则又按照createTime倒序
     * @param searchName
     * @return
     */
    @RequestMapping(value = "services/appliance/searchName", method = RequestMethod.GET)
    public AjaxResult list(@RequestParam("searchName") String searchName) {
        if (StringUtil.isBlank(searchName)) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_QXCXMCBNWK"));
        }
        searchName = searchName.toUpperCase();
        List<Appliance> applianceList = applianceService.listBySearchName(searchName);
        return AjaxResult.success(applianceList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_CXCG"));
    }

    @RequestMapping(value = "appliance", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装
        List<Appliance> applianceList = applianceService.lists(whereRequest);
        PageInfo<Appliance> page = new PageInfo<Appliance>(applianceList);

        return AjaxResult.success(page, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_CXCG"));
    }

    @RequestMapping(value = "appliance/import", method = RequestMethod.POST)
    public AjaxResult importApplianceExcel(@RequestParam("file") MultipartFile file) {
        try {
            File uploadFile = saveUploadFile(APPLIANCE_IMPORT_FILE_PATH, file);
            //校验文件格式
            AjaxResult validateResult = validate(ApplianceServiceImpl.getExcelTileProperties(localeMessageSourceService), uploadFile);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }
            boolean result = applianceService.importExcel(uploadFile);
            return result ? AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRCG")) : AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        }
    }

    /**
     * 手术类型excel数据导入,因为文件上传代码一样，所以将次接口写到当前controller
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "operation/type/import", method = RequestMethod.POST)
    public AjaxResult importOperationTypeExcel(@RequestParam("file") MultipartFile file) {
        try {
            File uploadFile = saveUploadFile(OPERATION_IMPORT_FILE_PATH, file);
            //校验文件格式
            AjaxResult validateResult = validate(localeMessageSourceService.getMessage(OperationTypeServiceImpl.EXCEL_TITLE), uploadFile);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }
            boolean result = operationTypeService.importExcel(uploadFile);
            return result ? AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRCG")) : AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        }
    }

    /**
     * 手术类型及默认器械excel数据导入,因为文件上传代码一样，所以将次接口写到当前controller
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "operation/defaultAppliance/import", method = RequestMethod.POST)
    public AjaxResult importOperationDefaultAppliance(@RequestParam("file") MultipartFile file) {
        try {
            File uploadFile = saveUploadFile(OPERATION_DEFAULT_APPLIANCE_IMPORT_FILE_PATH, file);
            //校验文件格式
            AjaxResult validateResult = validate(localeMessageSourceService.getMessage(OperationTypeServiceImpl.OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE), uploadFile);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }
            boolean result = operationTypeService.importOperationDefaultApplianceExcel(uploadFile);
            return result ? AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRCG")) : AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRWJCC"));
        }
    }

    private File saveUploadFile(String dirName, MultipartFile file) throws Exception {
        if (null == file || file.isEmpty()) {
            return null;
        }
        File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator +
                SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + dirName);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        LOGGER.info("createResource dest.path ={} ", dest.getPath());
        String fileName = file.getOriginalFilename();
        dest = FileUtils.getFile(dest.getPath() + File.separator + fileName);
        LOGGER.info("createResource dest.path with fileName ={} ", dest.getPath());
        if (!dest.exists()) {
            dest.createNewFile();
        }
        org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
        //修改文件上传undertow不兼容问题
//        file.transferTo(dest);
        return dest;
    }

    /**
     * 校验文件格式和excel表头信息
     *
     * @param uploadFile
     * @return
     */
    private AjaxResult validate(String[] titles, File uploadFile) {
        if (null == uploadFile) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_SCWJWK"));
        }
        boolean isExcelFile = ExcelUtil.isExcelFile(uploadFile.getName());
        if (!isExcelFile) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRSBQSCEXCELWJ"));
        }
        //校验excel表头
        List<String> headerList = ExcelUtil.getTableSheetDeader(uploadFile);
        boolean validateResult = validateHeader(titles, headerList);
        if (!validateResult) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_DRSBSJMBBPPQCZMBSCSJ"));
        }
        return AjaxResult.success();
    }

    /**
     * 数据的导入模板下载
     *
     * @return
     */
    @RequestMapping(value = "appliance/import/templateDownload", method = RequestMethod.GET)
    public AjaxResult templateDownload() {
        String url = DOWNLOAD_HTTP + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_100TEMPLATESQXMXDRSJMBXLSX");
        LOGGER.info("额外器械数据的导入模板下载地址= " + url);
        return AjaxResult.success(url, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_appliance_controller_ApplianceController_java_CZCG"));
    }

    private static boolean validateHeader(String[] titles, List<String> headerList) {
        if (headerList.size() != titles.length) {
            return false;
        }
        List<String> titleList = Arrays.asList(titles);
        if (titleList.containsAll(headerList)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     *  根据手术室编号，获取推荐的者器械类型，根据器械的使用次数进行排序
     *
     * @return
     */
    @RequestMapping(value = "erp/appliance/recommend", method = RequestMethod.GET)
    public AjaxResult recommend(@RequestParam("stationId") Long stationId) {
        List<Appliance> applianceList = applianceService.recommend(stationId);
        return AjaxResult.success(applianceList,"操作成功");
    }
}
