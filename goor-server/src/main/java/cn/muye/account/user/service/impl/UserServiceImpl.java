package cn.muye.account.user.service.impl;

import cn.mrobot.bean.account.*;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.dto.area.station.StationDTO4User;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.role.service.RoleService;
import cn.muye.account.role.service.UserStationXrefService;
import cn.muye.account.user.mapper.UserMapper;
import cn.muye.account.user.service.UserRoleXrefService;
import cn.muye.account.user.service.UserService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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

    @Autowired
    private StationService stationService;

    private static final int ACTIVATED = 1; //有效

    @Override
    public List<User> getUser(String userName, String password) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("USER_NAME =", userName);
        example.createCriteria().andCondition("PASSWORD =", password);
        List<User> userList = userMapper.selectByExample(example);
        if (userList != null && userList.size() > 0) {
            for (User u : userList) {
                List<UserStationXref> userStationXrefDbList = userStationXrefService.getByUserId(u.getId());
                UserRoleXref userRoleXrefDb = userRoleXrefService.getByUserId(u.getId());
                if (userRoleXrefDb != null) {
                    Role roleDb = roleService.getById(userRoleXrefDb.getRoleId());
                    if (roleDb != null) {
                        u.setRoleId(roleDb.getId());
                        u.setRoleName(roleDb.getCnName());
                    }
                }
                List<StationDTO4User> stationList = new ArrayList<>();
                addToStationList(userStationXrefDbList, stationList);
                u.setStationList(stationList);

            }
        }
        return userList;
    }

    /**
     * 新增用户
     * @param user
     */
    @Override
    public void addUser(User user) {
        user.setActivated(true);
        user.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        user.setCreateTime(new Date());
        user.setCreatedBy(1L);
        user.setCreateTime(new Date());
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
        /*if (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
            //删掉之前的user绑定station
            userStationXrefService.deleteByUserId(userId);
            List<StationDTO4User> stationIdList = user.getStationList();
            saveUserStationXref(stationIdList, userId);
        }*/
        //todo 这段保存用户站点关系的代码暂时启用，以后用户还是可以绑定多个站的
        userStationXrefService.deleteByUserId(userId);
        List<StationDTO4User> stationIdList = user.getStationList();
        saveUserStationXref(stationIdList, userId);
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
        List<StationDTO4User> stationList = user.getStationList();
        Role roleDb = roleService.getById(roleId);
        user.setRoleName(roleDb.getCnName());
        //todo 暂时先删除用户站关联
        userStationXrefService.deleteByUserId(userId);
        saveUserStationXref(stationList, userId);
        /*if (userRoleXrefDb != null) {
            ////如果原来角色是3，变更角色是2就需要把user_station_xref的记录删掉
            if (userRoleXrefDb.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && roleId.equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()))) {
                userStationXrefService.deleteByUserId(userId);
            }
            if (userRoleXrefDb.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && roleId.equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && stationList != null && stationList.size() > 0) {
                userStationXrefService.deleteByUserId(userId);
                saveUserStationXref(stationList, userId);
            }
            ////如果原来角色是2，变更角色是3就需要增加一条user_station_xref的记录
            if (userRoleXrefDb != null && userRoleXrefDb.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption())) && roleId.equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && stationList != null && stationList.size() > 0) {
                saveUserStationXref(stationList, userId);
            }
            userRoleXrefDb.setRoleId(roleId);
            userRoleXrefService.update(userRoleXrefDb);
        } else {
            userRoleXrefDb = new UserRoleXref();
            userRoleXrefDb.setUserId(userId);
            userRoleXrefDb.setRoleId(roleId);
            userRoleXrefService.save(userRoleXrefDb);
            if (roleId.equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && stationList != null && stationList.size() > 0) {
                saveUserStationXref(stationList, userId);
            }
        }*/
    }

    /**
     * 保存用户站关联记录
     * @param stationList
     * @param userId
     */
    private void saveUserStationXref(List<StationDTO4User> stationList, Long userId) {
        if (stationList != null && stationList.size() > 0) {
            for (StationDTO4User stationDTO4User : stationList) {
                UserStationXref userStationXref = new UserStationXref();
                userStationXref.setUserId(userId);
                userStationXref.setStationId(stationDTO4User.getId());
                userStationXrefService.save(userStationXref);
            }
        }
    }

    @Override
    public List<User> list(WhereRequest whereRequest, User user) {
        PageHelper.startPage(whereRequest.getPage(),whereRequest.getPageSize());
        Map map = new HashMap<>();
        if (!StringUtil.isNullOrEmpty(whereRequest.getQueryObj())) {
            JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
            String name = (String)jsonObject.get(SearchConstants.SEARCH_NAME);
            if (!StringUtil.isNullOrEmpty(name)) {
                map.put("name", name);
            }
        };
        List<User> userList = null;
        if (user != null) {
            //如果是超级管理员
            if (user.getRoleId() != null) {
              if (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
                  //todo 以后从切换门店的Session里拿
                  Long storeId = user.getStoreId();
                  map.put("storeId", storeId);
                  userList = userMapper.selectBySuperAdmin(map);
              } else if (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()))) {
                  //只拿自己和storeId相同的站管理员
                  Long storeId = user.getStoreId();
                  //加上所有role_id是3的storeId等于storeId的
                  map.put("userId", user.getId());
                  map.put("storeId", storeId);
                  map.put("roleId", Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
                  userList = userMapper.selectByHospitalAdmin(map);
              } else if (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
                  //只拿自己
                  map.put("userId", user.getId());
                  userList = userMapper.selectByStationAdmin(map);
              } else {
              }
              }
            }
            if (userList != null && userList.size() > 0) {
                for (User u : userList) {
                    List<StationDTO4User> stationList = new ArrayList<>();
                    List<UserStationXref> userStationXrefDbList = userStationXrefService.getByUserId(u.getId());
                    UserRoleXref xref = userRoleXrefService.getByUserId(u.getId());
                    if (xref != null) {
                        Role roleDb = roleService.getById(xref.getRoleId());
                        if (roleDb != null) {
                            u.setRoleId(roleDb.getId());
                            u.setRoleName(roleDb.getCnName());
                        }
                    }
                    addToStationList(userStationXrefDbList, stationList);
                    u.setStationList(stationList);
                }
            }
            return userList;
    }

    /**
     * 从用户站点关系表中封装成stationList，set到用户实体
     * @param userStationXrefDbList
     * @param stationList
     */
    private void addToStationList(List<UserStationXref> userStationXrefDbList, List<StationDTO4User> stationList) {
        if (userStationXrefDbList != null && userStationXrefDbList.size() > 0) {
            for (UserStationXref ux : userStationXrefDbList) {
                Station stationDb = stationService.findById(ux.getStationId());
                stationList.add(stationToDTO(stationDb));
            }
        }
    }

    /**
     *
     * @param station
     * @return
     */
    public static StationDTO4User stationToDTO(Station station) {
        StationDTO4User stationDTO4User = new StationDTO4User();
        stationDTO4User.setId(station.getId());
        stationDTO4User.setName(station.getName());
        return stationDTO4User;
    }


    @Override
    public User getUserByDirectKey(Integer directKey, Long storeId) {
        return userMapper.getUserByDirectKey(directKey, storeId);
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
