package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.order.Goods;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.base.controller.BaseController;
import cn.muye.order.service.GoodsService;
import com.github.pagehelper.PageInfo;
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

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsTypeService goodsTypeService;

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
            return AjaxResult.success(goods, "获取货物成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取货物出错");
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
            return AjaxResult.success(goodsTypeList,"获取货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取货物类型出错");
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
            return AjaxResult.success(goodsTypeList,"获取货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取货物类型出错");
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
            return AjaxResult.success(goodsList,"获取货物成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取货物出错");
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
    public AjaxResult saveGoodsType(@RequestBody GoodsType goodsType, HttpServletRequest request){
        try {
            goodsTypeService.save(goodsType, request);
            return AjaxResult.success(goodsType, "保存货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("保存货物类型出错");
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
            return AjaxResult.success(goodsType, "修改货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("修改货物类型出错");
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
            return AjaxResult.success("删除货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("删除货物类型出错");
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
            return AjaxResult.success(pageResult, "分页查询货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("分页查询货物类型出错");
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
    public AjaxResult saveGoods(@RequestBody Goods goods, HttpServletRequest request){
        try {
            goodsService.save(goods, request);
            return AjaxResult.success(goods, "保存货物成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("保存货物出错");
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
            return AjaxResult.success(goods, "修改货物成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("修改货物出错");
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
            return AjaxResult.success("删除货物成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("删除货物出错");
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
            return AjaxResult.success(pageResult, "分页查询货物类型成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("分页查询货物类型出错");
        }
    }

}
