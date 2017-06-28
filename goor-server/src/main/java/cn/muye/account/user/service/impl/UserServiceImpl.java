package cn.muye.account.user.service.impl;

import cn.mrobot.bean.account.Role;
import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserRoleXref;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.role.service.RoleService;
import cn.muye.account.user.mapper.UserMapper;
import cn.muye.account.user.service.UserRoleXrefService;
import cn.muye.account.user.service.UserService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleXrefService userRoleXrefService;

    @Autowired
    private RoleService roleService;

    @Override
    public List<User> getUser(String userName, String password) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("USER_NAME =", userName);
        example.createCriteria().andCondition("PASSWORD =", password);
        return userMapper.selectByExample(example);
    }

    @Override
    public void addUser(User user) {
        user.setActivated(true);
        user.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        user.setCreated(new Date());
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public List<User> list(WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(),whereRequest.getPageSize());
        Example example = new Example(User.class);
        example.createCriteria().andCondition("ACTIVATED =", 1);
        List<User> userList = userMapper.selectByExample(example);

        if (userList != null && userList.size() > 0) {
            for (User u : userList) {
                UserRoleXref xref = userRoleXrefService.getByUserId(u.getId());
                if (xref != null) {
                    Role roleDb = roleService.getById(xref.getRoleId());
                    if (roleDb != null) {
                        u.setUserType(roleDb.getRoleType().equals(Constant.SUPER_ADMIN_ROLE_ID)? Constant.USER_TYPE_SUPER_ADMIN : Constant.USER_TYPE_NORMAL_ADMIN);
                    }
                }
            }
        }
        return userList;
    }

    @Override
    public User getUserByDirectKey(Integer directKey) {
        return userMapper.getUserByDirectKey(directKey);
    }

    @Override
    public User getByUserName(String username) {
        return userMapper.getByUserName(username);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public void fakeDeleteById(Long id) {
        userMapper.fakeDeleteById(id);
    }

    @Override
    public User bindRole(String userId, String roleId) {
        //删除原来的绑定
        UserRoleXref userRoleXrefDb = userRoleXrefService.getByUserId(Long.valueOf(userId));
        if (userRoleXrefDb != null) {
            userRoleXrefDb.setRoleId(Long.valueOf(roleId));
            userRoleXrefService.update(userRoleXrefDb);
        } else {
            UserRoleXref userRoleXref = new UserRoleXref();
            userRoleXref.setRoleId(Long.valueOf(roleId));
            userRoleXref.setUserId(Long.valueOf(userId));
            userRoleXrefService.save(userRoleXref);
        }
        Role roleDb = roleService.getById(Long.valueOf(roleId));
        String roleName = null;
        if (roleDb != null) {
            roleName = roleDb.getCnName();
        }
        User userDb = userMapper.selectByPrimaryKey(Long.valueOf(userId));
        if (userDb != null) {
            userDb.setUserName(roleName);
        }
        return userDb;
    }
}
