package cn.muye.shiro.service;

import cn.muye.shiro.bean.Permission;
import cn.muye.shiro.bean.Role;
import cn.muye.shiro.bean.RolePermission;
import cn.muye.shiro.bean.UserRole;
import cn.muye.shiro.mapper.PermissionMapper;
import cn.muye.shiro.mapper.RoleMapper;
import cn.muye.shiro.mapper.RolePermissionMapper;
import cn.muye.shiro.mapper.UserRoleMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
@Service
@Transactional
public class ShiroService {

    @Autowired
    private UserRoleMapper adminUserRoleMapper;

    @Autowired
    private RoleMapper adminRoleMapper;

    @Autowired
    private PermissionMapper adminPermissionMapper;

    @Autowired
    private RolePermissionMapper adminRolePermissionMapper;

    public List<Role> listRoles() {
        return adminRoleMapper.listRole();
    }

    public Role getRoleByRoleId(Long id) {
        return adminRoleMapper.getById(id);
    }

    public List<Permission> listPermissions(Integer page) {
        return adminPermissionMapper.listPermissions(page);
    }

    public List<UserRole> listUserRolesByUserId(Long id) {
        return adminUserRoleMapper.listUserRolesByUserId(id);
    }

    public List<Permission> listPermissionsByRoleId(Long id) {
        List<RolePermission> rolePermissionList = adminRolePermissionMapper.listPermissionsByRoleId(id);
        List<Long> permissionIdList = Lists.newArrayList();
        List<Permission> permissionList = null;
        if (rolePermissionList != null && rolePermissionList.size() > 0) {
            for (RolePermission rolePermission : rolePermissionList) {
                permissionIdList.add(rolePermission.getpId());
            }
            permissionList = adminPermissionMapper.listPermissionsByIds(permissionIdList);
        }
        return permissionList;
    }

    public Role getById(Long id) {
        return adminRoleMapper.getById(id);
    }

    public void update(Role roleDb) {
        adminRoleMapper.update(roleDb);
    }

    public void save(Role role) {
        adminRoleMapper.save(role);
    }

    public void bindRolePermission(Long roleId, List<Long> permissionIdList) {
        adminRolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIdList != null && permissionIdList.size() > 0) {
            for (Long permissionId : permissionIdList) {
                Map map = Maps.newHashMap();
                map.put("roleId", roleId);
                map.put("permissionId", permissionId);
                adminRolePermissionMapper.bindRolePermission(map);
            }
        }
    }

    public void bindUserRole(Long roleId, Long userId) {
        adminUserRoleMapper.deleteByUserId(userId);
        if (roleId != null) {
            Map map = Maps.newHashMap();
            map.put("roleId", roleId);
            map.put("userId", userId);
            adminUserRoleMapper.bindUserRole(map);
        }
    }
}
