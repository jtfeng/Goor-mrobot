package cn.muye.account.role.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Permission;
import cn.mrobot.bean.account.Role;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.permission.service.PermissionService;
import cn.muye.account.role.service.RoleService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
@RestController
public class RoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = {"account/role"}, method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('account_role_r')")
    @ApiOperation(value = "查询角色接口", httpMethod = "GET", notes = "查询角色接口")
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        List<Role> list = roleService.listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Role.class, "ID desc");
        PageInfo<Role> pageList = new PageInfo<>(list);
        return AjaxResult.success(pageList, "查询成功");
    }

    @RequestMapping(value = {"account/role"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_role_u')")
    @ApiOperation(value = "新增或修改角色接口", httpMethod = "POST", notes = "新增或修改角色接口")
    @ResponseBody
    public AjaxResult addOrUpdateRole(@RequestBody Role role) {
        if (role == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
        }
        Role roleDb = roleService.getByCnName(role.getCnName());
        if (roleDb != null && roleDb.getCnName().equals(role.getCnName()) && !roleDb.getId().equals(role.getId())) {
            return AjaxResult.failed("用户名重复");
        }
        Long id = role.getId();
        if (id == null) { //新增
            roleService.save(role);
            return AjaxResult.success(role, "新增成功");
        } else {
            Role roleDbById = roleService.getById(id);
            if (roleDbById != null) {
                roleDbById.setCnName(role.getCnName());
                roleService.updateByStoreId(roleDbById);
                return AjaxResult.success(roleDbById, "修改成功");
            } else {
                return AjaxResult.failed("该角色不存在");
            }
        }
    }

    @RequestMapping(value = {"account/role/{id}"}, method = RequestMethod.DELETE)
//    @PreAuthorize("hasAuthority('account_role_d')")
    @ApiOperation(value = "删除角色接口", httpMethod = "DELETE", notes = "删除角色接口")
    @ResponseBody
    public AjaxResult delete(@PathVariable String id) {
        if (StringUtil.isNullOrEmpty(id)) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
        }
        try {
            roleService.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            return AjaxResult.failed("该角色有绑定关系，无法删除");
        }
        return AjaxResult.success("删除成功");
    }

    @RequestMapping(value = {"account/role/bindPermission"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_role_u')")
    @ApiOperation(value = "角色绑定权限接口", httpMethod = "POST", notes = "角色绑定权限接口")
    @ResponseBody
    public AjaxResult bindPermission(@RequestParam(value = "permissionIds") String permissionIds, @RequestParam(value = "roleId") Long roleId) {
        try {
            //数据库查询相应角色是否存在
            Role role = roleService.findById(roleId);
            if (role == null) {
                return AjaxResult.failed("角色不存在");
            }
            List<Long> permissionIdList = JSON.parseArray(permissionIds, Long.class);
            List<Permission> permissionList = permissionService.listByIds(permissionIdList);
            Set<Long> permissionIdSet = Sets.newHashSet();
            permissionList.forEach(permission -> {
                permissionIdSet.add(permission.getId());
            });
            roleService.bindPermission(permissionIdSet, role.getId());
        } catch (Exception e) {
            return AjaxResult.failed("该角色有绑定关系，无法删除");
        }
        return AjaxResult.success("绑定成功");
    }

}
