package cn.muye.account.permission.service.impl;

import cn.mrobot.bean.account.Permission;
import cn.muye.account.permission.mapper.PermissionMapper;
import cn.muye.account.permission.service.PermissionService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ray.fu on 2018/2/26.
 */
@Service
@Transactional
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> listByIds(List<Long> permissionIdList) {
        return permissionMapper.listByIds(permissionIdList);
    }
}
