package cn.muye.account.role.service;

import cn.mrobot.bean.account.Role;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Set;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface RoleService extends BaseService<Role> {

    Role getById(Long id);

    Role getByCnName(String cnName);

    void bindPermission(Set<Long> permissionIdSet, Long id);

    List<Role> listByIds(List<Long> roleIdList);
}
