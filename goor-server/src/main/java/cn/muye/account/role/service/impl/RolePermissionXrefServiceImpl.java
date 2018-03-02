package cn.muye.account.role.service.impl;

import cn.mrobot.bean.account.RolePermissionXref;
import cn.muye.account.role.mapper.RolePermissionXrefMapper;
import cn.muye.account.role.service.RolePermissionXrefService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ray.fu on 2018/2/27.
 */
@Service
@Transactional
public class RolePermissionXrefServiceImpl extends BaseServiceImpl<RolePermissionXref> implements RolePermissionXrefService {

    @Autowired
    private RolePermissionXrefMapper rolePermissionXrefMapper;

}
