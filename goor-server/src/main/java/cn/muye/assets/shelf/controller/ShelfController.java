package cn.muye.assets.shelf.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.util.SessionUtil;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/20.
 */
@Controller
public class ShelfController {

    private static Logger LOGGER = Logger.getLogger(ShelfController.class);

    @Autowired
    private ShelfService shelfService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = "assets/shelf", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        PageInfo<Shelf> pageList = null;
        try {
            Long sceneId = SessionUtil.getScene().getId();
            List<Shelf> list = shelfService.listPageByStoreIdAndOrderAndSceneId(whereRequest.getPage(), whereRequest.getPageSize(), whereRequest.getQueryObj(), Shelf.class, "ID DESC", sceneId);
            pageList = new PageInfo<>(list);
        } catch (Exception e) {
            LOGGER.error("ShelfController-list方法报错,{}", e);
        } finally {
        }
        return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_CXCG"));
    }

    @RequestMapping(value = "assets/shelf", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addOrUpdateShelf(@RequestBody Shelf shelf) {
        try {
            if (StringUtil.isNullOrEmpty(shelf.getCode()) || StringUtil.isNullOrEmpty(shelf.getRfid()) || StringUtil.isNullOrEmpty(shelf.getName()) || StringUtil.isNullOrEmpty(shelf.getType())) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_HJMCHRFIDHBHHLXBNWK"));
            }
            //判断是否有重复的名称
            Shelf shelfDbByName = shelfService.getByName(shelf.getName());
            if (shelfDbByName != null && !shelfDbByName.getId().equals(shelf.getId())) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_HJMCZF"));
            }
            Long id = shelf.getId();
            //判断是否有重复的编号
            Shelf shelfDbByCode = shelfService.getByCode(shelf.getCode());
            if (shelfDbByCode != null && !shelfDbByCode.getId().equals(id)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_HJBHZF"));
            }
            shelf.setSceneId(SessionUtil.getScene().getId());
            if (id == null) {
                shelf.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                shelf.setCreateTime(new Date());
                shelf.setCreatedBy(1L);
                shelfService.save(shelf);
                return AjaxResult.success(shelf, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_XZCG"));
            } else {
                Shelf shelfDb = shelfService.getById(id);
                shelfDb.setGoodTypes(shelf.getGoodTypes());
                shelfDb.setName(shelf.getName());
                shelfDb.setCode(shelf.getCode());
                shelfDb.setRfid(shelf.getRfid());
                shelfDb.setType(shelf.getType());
                shelfDb.setDescription(shelf.getDescription());
                shelfService.updateByStoreId(shelfDb);
                return AjaxResult.success(shelfDb, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_XGCG"));
            }
        } catch (Exception e) {
            LOGGER.error("ShelfController-addOrUpdateShelf方法报错,{}", e);
        } finally {
        }
        return AjaxResult.failed();
    }

    @RequestMapping(value = "assets/shelf/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult delete(@PathVariable String id) {
        if (id == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_CSYW"));
        }
        shelfService.deleteById(Long.valueOf(id));
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_shelf_controller_ShelfController_java_SCCG"));
    }

}
