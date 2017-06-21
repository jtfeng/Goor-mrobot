package cn.muye.shiro.mapper;

import cn.muye.shiro.bean.Permission;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public interface PermissionMapper {

    List<Permission> listPermissions(Integer page);

    List<Permission> listPermissionsByIds(List<Long> permissionIdList);
}
