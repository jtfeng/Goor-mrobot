package cn.muye.account.permission.mapper;

import cn.mrobot.bean.account.Permission;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.fu on 2018/2/26.
 */

public interface PermissionMapper extends MyMapper<Permission> {

    List<Permission> listByIds(List<Long> permissionIdList);
}
