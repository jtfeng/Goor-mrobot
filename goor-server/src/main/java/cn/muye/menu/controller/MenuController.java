//package cn.muye.menu.controller;
//
//import cn.mrobot.bean.AjaxResult;
//import cn.mrobot.utils.StringUtil;
//import cn.muye.i18n.service.LocaleMessageSourceService;
//import cn.muye.menu.bean.Menu;
//import cn.muye.menu.service.MenuService;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import java.util.List;
//
///**
// * Created by Ray.Fu on 2017/6/12.
// */
//public class MenuController {
//
//    @Autowired
//    private MenuService menuService;
//    @Autowired
//    private LocaleMessageSourceService localeMessageSourceService;
//
//    @RequestMapping(value = "admin/menu/user/{id}", method = RequestMethod.GET)
//    @ApiOperation(value = "按用户查询菜单列表", httpMethod = "GET", notes = "按用户查询菜单列表")
//    public AjaxResult listMenuByUser(@ApiParam(value = "用户ID") @PathVariable String userId) {
//        if (StringUtil.isNullOrEmpty(userId)) {
//            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_menu_controller_MenuController_java_CXSB"));
//        }
//        List<Menu> list = menuService.listMenu();
//
//
//
//        return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_menu_controller_MenuController_java_CXCG"));
//    }
//
//
//}
