package cn.muye.erp.appliance.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.erp.appliance.service.ApplianceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    private static String APPLIANCE_IMPORT_FILE_PATH = "appliance_import_file";

    @RequestMapping(value = "appliance", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Appliance appliance) {
        if (null == appliance){
            return AjaxResult.failed("保存对象不能为空");
        }
        if (StringUtil.isBlank(appliance.getName()) ||
                appliance.getDepartmentTypeCode() == 0 ||
                appliance.getPackageTypeCode() == 0){
            return AjaxResult.failed("保存对象关键属性不能为空");
        }
        //根据name,departmentTypeCode,packageTypeCode进行重复校验
        List<Appliance> applianceList = applianceService.findByNameAndCode(appliance.getName(),
                appliance.getDepartmentTypeCode(), appliance.getPackageTypeCode());
        if (null != applianceList && applianceList.size() > 0 ){
            return AjaxResult.failed("添加失败，同名称，类别，包装类型数据已经存在");
        }

        appliance.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        appliance.setCreateTime(new Date());
        applianceService.save(appliance);
        return AjaxResult.success(appliance, "添加成功");
    }

    @RequestMapping(value = "appliance", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody Appliance appliance) {
        if (null == appliance){
            return AjaxResult.failed("修改对象不能为空");
        }
        if (StringUtil.isBlank(appliance.getName())){
            return AjaxResult.failed("修改对象名称不能为空");
        }
        if (null == appliance.getId()){
            return AjaxResult.failed("修改对象ID不能为空");
        }
        //根据name,departmentTypeCode,packageTypeCode进行重复校验
        List<Appliance> applianceList = applianceService.findByNameAndCode(appliance.getName(),
                appliance.getDepartmentTypeCode(), appliance.getPackageTypeCode());
        if (null != applianceList && applianceList.size() > 0 ){
            return AjaxResult.failed("修改失败，同名称，类别，包装类型数据已经存在");
        }

        Appliance applianceDB = applianceService.findApplianceById(appliance.getId());
        applianceDB.setName(appliance.getName());
        applianceDB.setSearchName(appliance.getSearchName());
        applianceDB.setDepartmentType(appliance.getDepartmentType());
        applianceDB.setPackageType(appliance.getPackageType());
        applianceDB.setCreateTime(new Date());
        applianceService.updateSelective(applianceDB);
        return AjaxResult.success(applianceDB, "修改成功");
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.GET)
    public AjaxResult get(@PathVariable Long id) {
        if (null == id){
            return AjaxResult.failed("ID不能为空");
        }
        Appliance applianceDB = applianceService.findApplianceById(id);
        return AjaxResult.success(applianceDB,"修改成功");
    }

    @RequestMapping(value = "appliance/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id){
            return AjaxResult.failed("ID不能为空");
        }
        applianceService.deleteById(id);
        return AjaxResult.success("删除成功");
    }

    @RequestMapping(value = "appliance/searchName", method = RequestMethod.GET)
    public AjaxResult list(@RequestParam("searchName") String  searchName) {
        if (StringUtil.isBlank(searchName)){
            return AjaxResult.failed("器械查询名称不能为空");
        }
    searchName = searchName.toUpperCase();
    List<Appliance> applianceList = applianceService.listBySearchName(searchName, SearchConstants.FAKE_MERCHANT_STORE_ID);
        return AjaxResult.success(applianceList,"查询成功");
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

        return AjaxResult.success(page,"查询成功");
    }

    @RequestMapping(value = "appliance/import", method = RequestMethod.POST)
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (null == file || file.isEmpty()) {
                return AjaxResult.failed("上传文件为空");
            }
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator +
                    SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + APPLIANCE_IMPORT_FILE_PATH);
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
            boolean result = applianceService.importExcel(dest);
            return result ? AjaxResult.success("导入成功") : AjaxResult.failed("导入文件出错");
        } catch (Exception e) {
            LOGGER.info("导入文件出错", e);
            return AjaxResult.failed("导入文件出错");
        }
    }
}
