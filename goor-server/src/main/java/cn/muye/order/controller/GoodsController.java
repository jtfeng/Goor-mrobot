package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.order.Goods;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.service.GoodsService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Selim on 2017/7/7.
 * 货物的 管理controller
 */
@Controller
@RequestMapping("goods")
public class GoodsController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsTypeService goodsTypeService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    /**
     * 获取货物 id
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getGoods(@RequestParam("id") Long id){
        try {
            Goods goods = goodsService.findById(id);
            return AjaxResult.success(goods, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWCC"));
        }
    }

    /**
     * 获取货物类型的list
     * @return
     */
    @RequestMapping(value = "listGoodsType",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listGoodsType(){
        try {
            List<GoodsType> goodsTypeList = goodsTypeService.listAll();
            goodsTypeList.forEach(goodsType -> goodsType.setGoodsList(goodsService.listGoodsByType(goodsType.getId())));
            return AjaxResult.success(goodsTypeList,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWLXCC"));
        }
    }

    /**
     * 获取货物类型的list（无查询具体货物）
     * @return
     */
    @RequestMapping(value = "listJustGoodsType",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listJustGoodsType(){
        try {
            List<GoodsType> goodsTypeList = goodsTypeService.listAll();
            return AjaxResult.success(goodsTypeList,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWLXCC"));
        }
    }

    /**
     * 获取货物类型的list
     * @return
     */
    @RequestMapping(value = "listGoodsByType",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listGoodsByType(@RequestParam("type") Long type){
        try {
            List<Goods> goodsList = goodsService.listGoodsByType(type);
            return AjaxResult.success(goodsList,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_HQHWCC"));
        }
    }

    //-------------------货物类型------------------------------------
    /**
     * 新增货物类型
     * @param goodsType
     * @return
     */
    @RequestMapping(value = "goodsType",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveGoodsType(@RequestBody GoodsType goodsType){
        try {
            goodsTypeService.save(goodsType);
            return AjaxResult.success(goodsType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_BCHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_BCHWLXCC"));
        }
    }

    /**
     * 修改货物类型
     * @param goodsType
     * @return
     */
    @RequestMapping(value = "goodsType",method = RequestMethod.PUT)
    @ResponseBody
    public AjaxResult updateGoodsType(@RequestBody GoodsType goodsType){
        try {
            goodsTypeService.updateSelectiveByStoreId(goodsType);
            return AjaxResult.success(goodsType, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_XGHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_XGHWLXCC"));
        }
    }

    /**
     * 删除货物类型
     * 逻辑删除
     * @param id
     * @return
     */
    @RequestMapping(value = "goodsType",method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult deleteGoodsType(@RequestParam("id") Long id){
        try {
            GoodsType goodsType = new GoodsType();
            goodsType.setId(id);
            goodsType.setDeleteStatus(Boolean.TRUE);
            goodsTypeService.updateSelectiveByStoreId(goodsType);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_SCHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_SCHWLXCC"));
        }
    }

    /**
     * 分页查询货物类型
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listPageGoodsType",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listPageGoodsType(WhereRequest whereRequest){
        try {
            GoodsType goodsType = new GoodsType();
            goodsType.setDeleteStatus(Boolean.FALSE);
            List<GoodsType> goodsTypes = goodsTypeService.listQueryPageByStoreIdAndOrder(whereRequest.getPage(),whereRequest.getPageSize(),goodsType,"CREATE_TIME DESC");
            PageInfo<GoodsType> pageResult = new PageInfo<>(goodsTypes);
            return AjaxResult.success(pageResult, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_FYCXHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_FYCXHWLXCC"));
        }
    }

    //-------------------货物--------------------------
    /**
     * 新增货物
     * @param goods
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveGoods(@RequestBody Goods goods){
        try {
            goodsService.save(goods);
            return AjaxResult.success(goods, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_BCHWCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_BCHWCC"));
        }
    }

    /**
     * 修改货物
     * @param goods
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public AjaxResult updateGoods(@RequestBody Goods goods){
        try {
            goodsService.updateSelectiveByStoreId(goods);
            return AjaxResult.success(goods, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_XGHWCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_XGHWCC"));
        }
    }

    /**
     * 删除货物
     * 逻辑删除
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult deleteGoods(@RequestParam("id") Long id){
        try {
            goodsService.fakeDelete(id);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_SCHWCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_SCHWCC"));
        }
    }

    /**
     * 分页查询货物类型
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listPageGoods",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listPageGoods(@RequestParam(required = false,value = "type")Long type,
                                    WhereRequest whereRequest){
        try {
            Goods goods = new Goods();
            goods.setDeleteStatus(Boolean.FALSE);
            goods.setGoodTypeId(type);
            List<Goods> goodsList = goodsService.listQueryPageByStoreIdAndOrder(whereRequest.getPage(),whereRequest.getPageSize(),goods,"CREATE_TIME DESC");
            goodsList.forEach(goodsItem -> {
                GoodsType goodsType = goodsTypeService.findById(goodsItem.getGoodTypeId());
                goodsItem.setGoodsTypeName(goodsType.getName());
            });
            PageInfo<Goods> pageResult = new PageInfo<>(goodsList);
            return AjaxResult.success(pageResult, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_FYCXHWLXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_GoodsController_java_FYCXHWLXCC"));
        }
    }

}
