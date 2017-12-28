package cn.muye.erp.appliance.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import cn.muye.erp.appliance.service.ApplianceService;
import cn.muye.erp.appliance.service.impl.ApplianceServiceImpl;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.erp.operation.service.impl.OperationTypeServiceImpl;
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
    private OperationTypeService operationTypeService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    private static String APPLIANCE_IMPORT_FILE_PATH = "appliance_import_file";
    private static String OPERATION_IMPORT_FILE_PATH = "operation_import_file";

    @RequestMapping(value = "appliance", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Appliance appliance) {
        if (null == appliance) {
            return AjaxResult.failed("保存对象不能为空");
        }
        if (StringUtil.isBlank(appliance.getName()) ||
                appliance.getDepartmentTypeCode() == 0 ||
                appliance.getPackageTypeId() == null) {
            return AjaxResult.failed("保存对象关键属性不能为空");
        }
        //校验包装类别
        AppliancePackageType appliancePackageType = appliancePackageTypeService.findTypeById(appliance.getPackageTypeId());
        if (null == appliancePackageType) {
            return AjaxResult.failed("包装类别不存在或已删除，请预添加");
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
        return AjaxResult.success(appliance, "添加成功");
    }

    @RequestMapping(value = "appliance", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody Appliance appliance) {
        if (null == appliance) {
            return AjaxResult.failed("修改对象不能为空");
        }
        if (StringUtil.isBlank(appliance.getName())) {
            return AjaxResult.failed("修改对象名称不能为空");
        }
        if (null == appliance.getId()) {
            return AjaxResult.failed("修改对象ID不能为空");
        }

        AjaxResult validateResult = validateAppliance(appliance);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        Appliance applianceDB = applianceService.findApplianceById(appliance.getId());
        String name = appliance.getName();
        applianceDB.setName(appliance.getName());
        applianceDB.setSearchName(StringUtil.getSearchName(name));
        applianceDB.setDepartmentType(appliance.getDepartmentType());
        applianceDB.setPackageType(appliance.getPackageType());
        applianceDB.setCreateTime(new Date());
        applianceService.updateSelective(applianceDB);
        return AjaxResult.success(applianceDB, "修改成功");
    }

    private AjaxResult validateAppliance(Appliance appliance) {
        //根据name,departmentTypeCode,packageTypeId进行重复校验
        List<Appliance> applianceList = applianceService.findByNameAndCode(appliance.getName(),
                appliance.getDepartmentTypeCode());
        if (null != applianceList && applianceList.size() > 0) {
            return AjaxResult.failed("修改失败，同名称，类别，包装类型数据已经存在");
        }
        return AjaxResult.success();
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.GET)
    public AjaxResult get(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        Appliance applianceDB = applianceService.findApplianceById(id);
        return AjaxResult.success(applianceDB, "查询成功");
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        applianceService.removeById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 根据用户输入的字母和数字，查询出匹配结果排序规则按照全匹配，头匹配，包含匹配排序，每一种匹配规则又按照createTime倒序
     * @param searchName
     * @return
     */
    @RequestMapping(value = "services/appliance/searchName", method = RequestMethod.GET)
    public AjaxResult list(@RequestParam("searchName") String searchName) {
        if (StringUtil.isBlank(searchName)) {
            return AjaxResult.failed("器械查询名称不能为空");
        }
        searchName = searchName.toUpperCase();
        List<Appliance> applianceList = applianceService.listBySearchName(searchName);
        return AjaxResult.success(applianceList, "查询成功");
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

        return AjaxResult.success(page, "查询成功");
    }

    @RequestMapping(value = "appliance/import", method = RequestMethod.POST)
    public AjaxResult importApplianceExcel(@RequestParam("file") MultipartFile file) {
        try {
            File uploadFile = saveUploadFile(APPLIANCE_IMPORT_FILE_PATH, file);
            //校验文件格式
            AjaxResult validateResult = validate(ApplianceServiceImpl.EXCEL_TITLE, uploadFile);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }
            boolean result = applianceService.importExcel(uploadFile);
            return result ? AjaxResult.success("导入成功") : AjaxResult.failed("导入文件出错");
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed("导入文件出错");
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
            AjaxResult validateResult = validate(OperationTypeServiceImpl.EXCEL_TITLE, uploadFile);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }
            boolean result = operationTypeService.importExcel(uploadFile);
            return result ? AjaxResult.success("导入成功") : AjaxResult.failed("导入文件出错");
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed("导入文件出错");
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
        file.transferTo(dest);
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
            return AjaxResult.failed("上传文件为空");
        }
        boolean isExcelFile = ExcelUtil.isExcelFile(uploadFile.getName());
        if (!isExcelFile) {
            return AjaxResult.failed("导入失败，请上传excel文件");
        }
        //校验excel表头
        List<String> headerList = ExcelUtil.getTableSheetDeader(uploadFile);
        boolean validateResult = validateHeader(titles, headerList);
        if (!validateResult) {
            return AjaxResult.failed("导入失败，数据模板不匹配，请参照模板上传数据");
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
        String url = DOWNLOAD_HTTP + "/100/templates/器械明细-导入数据模板.xlsx";
        LOGGER.info("额外器械数据的导入模板下载地址= " + url);
        return AjaxResult.success(url, "操作成功");
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
}
