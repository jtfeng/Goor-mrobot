package cn.muye.account.permission.service;

import cn.mrobot.bean.account.Permission;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Ray.fu on 2018/2/26.
 */
public interface PermissionService extends BaseService<Permission> {

    List<Permission> listByIds(List<Long> permissionIdList);
}
