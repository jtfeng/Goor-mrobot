package cn.muye.assets.rfidbracelet.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.rfidbracelet.mapper.RfidBraceletMapper;
import cn.muye.assets.rfidbracelet.service.RfidBraceletService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by admin on 2017/7/3.
 */
@RestController
public class RfidBraceletController {
    // TODO: 2017/7/3 Simulate user data sources
    public static final Map<String,Integer> USER_DATA_SOURCES = new HashMap<>();;
    static {
        // TODO: 2017/7/3 Call the interface to get the data
        for (int i=0;i<10;i++){USER_DATA_SOURCES.put("guest"+i,0);}
    }
    @Autowired
    private RfidBraceletService rfidBraceletService;//操作数据的具体对象

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 返回所有可用的用户
     * @return
     */
    @RequestMapping(value = "/assets/bracelet/availableUser", method = RequestMethod.GET)
    public List getAvailableUsers(){
        List list = new ArrayList();
        synchronized (RfidBraceletController.USER_DATA_SOURCES) {
            for (String key : USER_DATA_SOURCES.keySet()) {
                if (0 == USER_DATA_SOURCES.get(key)) {
                    list.add(key);
                }
            }
        }
        return list;
    }
    /**
     * 新增手环信息
     * @param rfidBracelet
     * @return
     */
    @RequestMapping(value = "/assets/bracelet", method = RequestMethod.POST)
    public AjaxResult createRfidBracelet(@RequestBody RfidBracelet rfidBracelet) {
        try {
            // TODO: 2017/7/3 判断传入的用户是否真正存在
            synchronized (RfidBraceletController.USER_DATA_SOURCES) {
                if (USER_DATA_SOURCES.get(rfidBracelet.getBracblbtUsername()) == null) {
                    return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_CRDYHBCZ"));
                }
            }
            rfidBracelet.setCreateTime(new Date());
            rfidBracelet.setStoreId(100l);
            rfidBraceletService.save(rfidBracelet);
            return AjaxResult.success(rfidBracelet, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_XZSHXXCG"));
        }catch (Exception e){
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_XZSHXXSB"));
        }
    }
    /**
     * 删除手环信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/assets/bracelet/{id}", method = RequestMethod.DELETE)
    public AjaxResult deleteRfidBracelet(@PathVariable("id") String id){
        try {
            rfidBraceletService.deleteById(Long.parseLong(id));
            return AjaxResult.success(id,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_SCSHXXCG"));
        }catch (Exception e){
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_SCSHXXSB"));
        }
    }
    /**
     * 修改手环信息
     * @return
     */
    @RequestMapping(value = "/assets/bracelet", method = RequestMethod.PUT)
    public AjaxResult updateRfidBracelet(@RequestBody RfidBracelet rfidBracelet){
        try {
            synchronized (RfidBraceletController.USER_DATA_SOURCES) {
                // TODO: 2017/7/3 判断传入的用户是否真正存在
                if (USER_DATA_SOURCES.get(rfidBracelet.getBracblbtUsername()) == null) {
                    return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_CRDYHBCZ"));
                }
            }
            rfidBracelet.setStoreId(100l);
            rfidBraceletService.update(rfidBracelet);
            return AjaxResult.success(rfidBracelet,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_XGSHXXCG"));
        }catch (Exception e){
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_XGSHXXSB"));
        }
    }
    /**
     * 分页查询手环列表信息
     * @param whereRequest GET可以直接封装转换为对应的实体对象
     * @return
     */
    @RequestMapping(value = "/assets/bracelet", method = RequestMethod.GET)
    public AjaxResult rfidBraceletList(WhereRequest whereRequest) {
        try {
            List<RfidBracelet> list = rfidBraceletService.listRfidBracelet(whereRequest);
            PageInfo<RfidBracelet> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_CXCG"));
        }catch (Exception e){
            Example example = new Example(RfidBracelet.class);
            return AjaxResult.failed(e,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_rfidbracelet_controller_RfidBraceletController_java_CXSB"));
        }
    }
}