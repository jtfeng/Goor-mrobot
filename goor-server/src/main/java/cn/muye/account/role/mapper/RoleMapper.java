package cn.muye.account.role.mapper;

import cn.mrobot.bean.account.Role;
import cn.muye.util.MyMapper;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface RoleMapper extends MyMapper<Role> {

    Role getById(Long id);

    Role getByCnName(String cnName);
}
