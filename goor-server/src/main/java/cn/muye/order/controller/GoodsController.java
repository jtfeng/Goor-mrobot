package cn.muye.order.controller;

import cn.mrobot.bean.order.Goods;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.controller.BaseController;
import cn.muye.order.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Selim on 2017/7/7.
 * 货物的 管理controller
 */
@Controller
@RequestMapping("goods")
public class GoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;

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
     * 新增货物
     * @param goods
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveGoods(@RequestBody Goods goods){
        try {
            goodsService.save(goods);
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

}
