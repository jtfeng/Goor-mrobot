package cn.muye.account.role.service.impl;

import cn.mrobot.bean.account.Role;
import cn.mrobot.bean.account.RolePermissionXref;
import cn.muye.account.role.mapper.RoleMapper;
import cn.muye.account.role.mapper.RolePermissionXrefMapper;
import cn.muye.account.role.service.RoleService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
@Service
@Transactional
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionXrefMapper rolePermissionXrefMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.getById(id);
    }

    @Override
    public Role getByCnName(String cnName) {
        return roleMapper.getByCnName(cnName);
    }

    @Override
    public void bindPermission(Set<Long> permissionIdSet, Long id) {
        //删除原来的绑定关系
        RolePermissionXref rolePermissionXrefDb = rolePermissionXrefMapper.selectByPrimaryKey(id);
        if (rolePermissionXrefDb != null) {
            rolePermissionXrefMapper.deleteByPrimaryKey(id);
        }
        if (permissionIdSet == null) {
            return;
        }
        for (Long permissionId : permissionIdSet) {
            RolePermissionXref rolePermissionXref = new RolePermissionXref();
            rolePermissionXref.setRoleId(id);
            rolePermissionXref.setPermissionId(permissionId);
            rolePermissionXrefMapper.insert(rolePermissionXref);
        }
    }

    @Override
    public List<Role> listByIds(List<Long> roleIdList) {
        return roleMapper.listByIds(roleIdList);
    }

}
