package cn.muye.account.user.service.impl;

import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserRoleXref;
import cn.muye.account.user.mapper.UserRoleXrefMapper;
import cn.muye.account.user.service.UserRoleXrefService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
@Service
@Transactional
public class UserRoleXrefServiceImpl implements UserRoleXrefService {

    @Autowired
    private UserRoleXrefMapper userRoleXrefMapper;

    @Override
    public List<UserRoleXref> list() {
        return null;
    }

    @Override
    public UserRoleXref getByUserId(Long id) {
        return userRoleXrefMapper.getByUserId(id);
    }

    @Override
    public void save(UserRoleXref userRoleXref) {
        userRoleXrefMapper.insert(userRoleXref);
    }

    @Override
    public void update(UserRoleXref userRoleXref) {
        userRoleXrefMapper.updateByPrimaryKeySelective(userRoleXref);
    }
}
