package cn.muye.account.permission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Permission;
import cn.muye.account.permission.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Ray.fu on 2018/2/27.
 */
@Controller
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = {"account/permission"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_permission_r')")
    @ResponseBody
    public AjaxResult list() {
        List<Permission> list = permissionService.listAll();
        return AjaxResult.success(list,"查询成功");
    }
}
