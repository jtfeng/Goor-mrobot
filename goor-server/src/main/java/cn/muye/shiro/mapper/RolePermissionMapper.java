package cn.muye.shiro.mapper;

import cn.muye.shiro.bean.RolePermission;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public interface RolePermissionMapper {

    List<RolePermission> listPermissionsByRoleId(Long id);

    void bindRolePermission(Map map);

    void deleteByRoleId(Long roleId);
}
