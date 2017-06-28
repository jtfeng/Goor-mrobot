package cn.muye.account.role.service;

import cn.mrobot.bean.account.Role;
import cn.muye.base.service.BaseService;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface RoleService extends BaseService<Role> {

    Role getById(Long id);

    Role getByCnName(String cnName);

}
