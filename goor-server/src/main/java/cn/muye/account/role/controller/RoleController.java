package cn.muye.account.role.controller;

import cn.mrobot.bean.account.Role;
import cn.mrobot.bean.account.User;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.role.service.RoleService;
import cn.muye.account.role.service.RoleStationXrefService;
import cn.muye.area.station.controller.StationController;
import cn.muye.base.bean.AjaxResult;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
@Controller
public class RoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleStationXrefService roleStationXrefService;

    @RequestMapping(value = {"account/role"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色接口", httpMethod = "GET", notes = "查询角色接口")
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        List<Role> list = roleService.listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Role.class, "ID desc");
        PageInfo<Role> pageList = new PageInfo<>(list);
        return AjaxResult.success(pageList, "查询成功");
    }

    @RequestMapping(value = {"account/role"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改角色接口", httpMethod = "POST", notes = "新增或修改角色接口")
    @ResponseBody
    public AjaxResult addOrUpdateRole(@RequestBody Role role) {
        if (role == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
        }
        Role roleDb = roleService.getByCnName(role.getCnName());
        if (roleDb!= null && roleDb.getCnName().equals(role.getCnName()) && !roleDb.getId().equals(role.getId())) {
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

    @RequestMapping(value = {"account/role/station"}, method = RequestMethod.POST)
    @ApiOperation(value = "角色绑定站接口", httpMethod = "POST", notes = "角色绑定站接口")
    @ResponseBody
    public AjaxResult bindStation(@RequestParam String roleId, @RequestParam String stationId) {
        if (StringUtil.isNullOrEmpty(roleId) || StringUtil.isNullOrEmpty(stationId)) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
        }
        try {
            roleStationXrefService.save(Long.valueOf(roleId), Long.valueOf(stationId));
            return AjaxResult.success("绑定成功");
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage());
            return AjaxResult.failed("绑定失败");
        } finally {
        }
    }
}
