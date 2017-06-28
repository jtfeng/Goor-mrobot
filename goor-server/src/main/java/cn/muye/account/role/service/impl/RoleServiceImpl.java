package cn.muye.account.role.service.impl;

import cn.mrobot.bean.account.Role;
import cn.muye.account.role.mapper.RoleMapper;
import cn.muye.account.role.service.RoleService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
@Service
@Transactional
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.getById(id);
    }

    @Override
    public Role getByCnName(String cnName) {
        return roleMapper.getByCnName(cnName);
    }

}
