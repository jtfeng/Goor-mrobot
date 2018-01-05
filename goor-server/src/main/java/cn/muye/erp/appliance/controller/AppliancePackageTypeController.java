package cn.muye.erp.appliance.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import cn.muye.erp.appliance.service.ApplianceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/18
 */
@RestController
public class AppliancePackageTypeController {

    private static Logger LOGGER = LoggerFactory.getLogger(AppliancePackageTypeController.class);

    @Autowired
    private AppliancePackageTypeService appliancePackageTypeService;

    @Autowired
    private ApplianceService applianceService;

    @RequestMapping(value = "appliance/packageType", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody AppliancePackageType appliancePackageType) {
        String name = appliancePackageType.getName();
        AppliancePackageType appliancePackageTypeDB = appliancePackageTypeService.findByName(name);
        if (appliancePackageTypeDB != null) {
            return AjaxResult.failed("(" + name + ")该包装类型已经存在，请勿重复添加！");
        }
        appliancePackageType.setDeleteFlag(Constant.NORMAL);
        appliancePackageType.setCreateTime(new Date());
        appliancePackageType.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        appliancePackageTypeService.save(appliancePackageType);
        return AjaxResult.success(appliancePackageType, "添加成功");
    }

    @RequestMapping(value = "appliance/packageType", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody AppliancePackageType appliancePackageType) {
        String name = appliancePackageType.getName();
        Long id = appliancePackageType.getId();
        if (null == id) {
            return AjaxResult.failed("ID不能为空");
        }
        AppliancePackageType appliancePackageTypeDB = appliancePackageTypeService.findTypeById(id);
        if (appliancePackageTypeDB == null) {
            return AjaxResult.failed("(" + name + ")该包装类型不存在");
        }
        AppliancePackageType packageType = appliancePackageTypeService.findByName(name);
        if (null != packageType && !id.equals(packageType.getId())) {
            return AjaxResult.failed("(" + name + ")该名称的包装类型存在");
        }
        appliancePackageTypeService.updateSelective(appliancePackageType);
        return AjaxResult.success(appliancePackageType, "修改成功");
    }

    @RequestMapping(value = "appliance/packageType/{id}", method = RequestMethod.GET)
    public AjaxResult findById(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("查询ID不能为空");
        }
        AppliancePackageType appliancePackageType = appliancePackageTypeService.findTypeById(id);
        return AjaxResult.success(appliancePackageType, "查询成功");
    }

    @RequestMapping(value = "appliance/packageType/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        //根据包装类别查询关联的额外器械，如果包装类型关联了额外器械,那么该不能删除（提示用户已关联到器械，无法删除）；如果该包装类型没有关联到任何某种手术器械，那么改包装类型可以修改和删除（需提示弹框提示）
        LOGGER.info("删除包装类型，id=" + id);
        List<Appliance> applianceList = applianceService.listByPackageTypeId(id);
        AppliancePackageType appliancePackageType = appliancePackageTypeService.findTypeById(id);
        if (null != applianceList && applianceList.size() > 0) {
            return AjaxResult.failed("包装类型(" + appliancePackageType.getName() + ")已关联到器械，无法删除");
        }
        appliancePackageTypeService.removeById(id);
        return AjaxResult.success("删除成功");
    }

    @RequestMapping(value = "appliance/packageType", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装
        List<AppliancePackageType> appliancePackageTypeList = appliancePackageTypeService.listAllPackageType(whereRequest);
        PageInfo<AppliancePackageType> page = new PageInfo<AppliancePackageType>(appliancePackageTypeList);

        return AjaxResult.success(page, "查询成功");
    }

}
