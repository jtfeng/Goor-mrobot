package cn.muye.account.user.service.impl;

import cn.mrobot.bean.account.Role;
import cn.mrobot.bean.account.UserStationXref;
import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserRoleXref;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.role.service.RoleService;
import cn.muye.account.role.service.UserStationXrefService;
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
import java.util.List;

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

    @Autowired
    private UserStationXrefService userStationXrefService;

    private static final int ACTIVATED = 1; //有效

    @Override
    public List<User> getUser(String userName, String password) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("USER_NAME =", userName);
        example.createCriteria().andCondition("PASSWORD =", password);
        return userMapper.selectByExample(example);
    }

    /**
     * 新增用户
     * @param user
     */
    @Override
    public void addUser(User user) {
        user.setActivated(true);
        user.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        user.setCreated(new Date());
        user.setCreatedBy(1L);
        user.setCreated(new Date());
        userMapper.insert(user);
        Long userId = user.getId();
        //保存用户角色
        UserRoleXref userRoleXref = new UserRoleXref();
        userRoleXref.setRoleId(user.getRoleId());
        userRoleXref.setUserId(userId);
        userRoleXrefService.save(userRoleXref);
        Role roleDb = roleService.getById(user.getRoleId());
        user.setRoleName(roleDb.getCnName());
        //如果角色是站管理员
        //todo 常量
        if (user.getRoleId().equals(3L)) {
            //删掉之前的user绑定station
            userStationXrefService.deleteByUserId(userId);
            String stationIds = user.getStationIds();
            saveUserStationXref(stationIds, userId);
        }
    }

    /**
     * 更新用户
     * @param user
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateByPrimaryKey(user);
        //更新用户角色
        Long userId = user.getId();
        UserRoleXref userRoleXrefDb = userRoleXrefService.getByUserId(userId);
        Long roleId = user.getRoleId();
        String stationIds = user.getStationIds();
        Role roleDb = roleService.getById(roleId);
        user.setRoleName(roleDb.getCnName());
        if (userRoleXrefDb != null) {
            ////如果原来角色是3，变更角色是2就需要把user_station_xref的记录删掉
            if (userRoleXrefDb.getRoleId().equals(3L) && roleId.equals(2L)) {
                userStationXrefService.deleteByUserId(userId);
            }
            if (userRoleXrefDb.getRoleId().equals(3L) && roleId.equals(3L) && !StringUtil.isNullOrEmpty(stationIds) && stationIds.split(",").length > 0) {
                userStationXrefService.deleteByUserId(userId);
                saveUserStationXref(stationIds, userId);
            }
            ////如果原来角色是2，变更角色是3就需要增加一条user_station_xref的记录
            if (userRoleXrefDb != null && userRoleXrefDb.getRoleId().equals(2L) && roleId.equals(3L) && !StringUtil.isNullOrEmpty(stationIds) && stationIds.split(",").length > 0) {
                saveUserStationXref(stationIds, userId);
            }
            userRoleXrefDb.setRoleId(roleId);
            userRoleXrefService.update(userRoleXrefDb);
        } else {
            userRoleXrefDb = new UserRoleXref();
            userRoleXrefDb.setUserId(userId);
            userRoleXrefDb.setRoleId(roleId);
            userRoleXrefService.save(userRoleXrefDb);
            if (roleId.equals(3L) && !StringUtil.isNullOrEmpty(stationIds) && stationIds.split(",").length > 0) {
                saveUserStationXref(stationIds, userId);
            }
        }
    }

    /**
     * 保存用户站关联记录
     * @param stationIds
     * @param userId
     */
    private void saveUserStationXref(String stationIds, Long userId) {
        for (String stationId : stationIds.split(",")) {
            UserStationXref userStationXref = new UserStationXref();
            userStationXref.setUserId(userId);
            userStationXref.setStationId(Long.valueOf(stationId));
            userStationXrefService.save(userStationXref);
        }
    }

    @Override
    public List<User> list(WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(),whereRequest.getPageSize());
        Example example = new Example(User.class);
        example.createCriteria().andCondition("ACTIVATED =", ACTIVATED);
        example.setOrderByClause("ID DESC");
        List<User> userList = userMapper.selectByExample(example);
        if (userList != null && userList.size() > 0) {
            for (User u : userList) {
                UserRoleXref xref = userRoleXrefService.getByUserId(u.getId());
                if (xref != null) {
                    Role roleDb = roleService.getById(xref.getRoleId());
                    if (roleDb != null) {
                        u.setRoleId(roleDb.getId());
                        u.setRoleName(roleDb.getCnName());
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
