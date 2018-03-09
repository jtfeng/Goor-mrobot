package cn.muye.account.user.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.*;
import cn.mrobot.bean.area.point.IndustrialControlPointType;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.assets.door.DoorType;
import cn.mrobot.bean.assets.rfidbracelet.RfidBraceletTypeEnum;
import cn.mrobot.bean.assets.robot.RobotTypeEnum;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.VersionConstants;
import cn.mrobot.bean.erp.appliance.ApplianceDepartmentType;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.mission.MissionListTypeEnum;
import cn.mrobot.bean.mission.MissionTypeEnum;
import cn.mrobot.bean.order.OrderSetting;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.bean.state.enums.StateFieldEnums;
import cn.mrobot.dto.account.RoleDTO;
import cn.mrobot.dto.account.UserDTO;
import cn.mrobot.dto.area.station.StationDTO4User;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.role.service.RoleService;
import cn.muye.account.user.service.UserRoleXrefService;
import cn.muye.account.user.service.UserService;
import cn.muye.account.user.service.impl.UserServiceImpl;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.erp.appliance.service.ApplianceDepartmentTypeService;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.service.OrderSettingService;
import cn.muye.util.EnumUtil;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Ray.Fu on 2017/6/13.
 */
@Controller
public class UserController implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private OrderSettingService orderSettingService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleXrefService userRoleXrefService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${authServer.host}")
    private String authServerHost;

    @Value("${authServer.port}")
    private String authServerPort;

    @Value("${authServer.api}")
    private String authServerApi;

    private static final int SOURCE_TYPE_LIST = 1; //列表来源

    private static final int SOURCE_TYPE_OTHER = 2; //其他来源

    private static final int LOGGED_IN = 1; //已登录
    private static final int LOGGED_OUT = 0; //未登录

    @Autowired
    private UserUtil userUtil;

    /**
     * 新增修改用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = {"account/user"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_user_u')")
    @ApiOperation(value = "新增修改用户接口", httpMethod = "POST", notes = "新增修改用户接口")
    @ResponseBody
    public AjaxResult addOrUpdateUser(@RequestBody User user) {
        List<StationDTO4User> stationList = user.getStationList();
        Long roleId = user.getRoleId();
        String userName = user.getUserName();
        Long userId = user.getId();
        Integer directLoginKey = user.getDirectLoginKey();
        try {
            if (user == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_CSYW"));
            }
            if (userId == null && StringUtil.isNullOrEmpty(userName)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHMHMMBNWK"));
            }
            if (roleId == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_JSBNWK"));
            }
            if (user.getRoleId() != null && !user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && user.getStationList() != null && user.getStationList().size() > 0) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_BSZGLYJSBNBDZ"));
            }
            if (roleId != null && roleId.equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && (stationList == null || (stationList != null && (stationList.size() > 1 || stationList.size() == 0)))) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_ZBNWKHZBNBDDGZ"));
            }
            if (roleId != null && roleId.equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_BNXZCJGLY"));
            }
            if (directLoginKey != null && directLoginKey > 9999 && directLoginKey < 1000) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_KJMMBXS4WS"));
            }
            User userDb = userService.getByUserName(userName);
            if (userDb != null && userDb.getUserName().equals(userName) && !userDb.getId().equals(user.getId())) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHMZF"));
            }
            //todo StoreId暂时用100去代替，以后用session里获取
            if (directLoginKey != null) {
                User userDbByDirectKey = userService.getUserByDirectKey(directLoginKey, SearchConstants.FAKE_MERCHANT_STORE_ID);
                if (userDbByDirectKey != null) {
                    if (directLoginKey != null && userDbByDirectKey.getDirectLoginKey().equals(directLoginKey) && !userDbByDirectKey.getId().equals(userId)) {
                        return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_4WKJMZF"));
                    }
                }
            }
            if (userId == null) {
                userService.addUser(user);
                return AjaxResult.success(entityToDto(user, SOURCE_TYPE_OTHER), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_XZCG"));
            } else {
                User userDbById = userService.getById(userId);
                if (userDbById != null) {
                    if (!StringUtil.isNullOrEmpty(user.getPassword())) {
                        userDbById.setPassword(user.getPassword());
                    }
                    userDbById.setRoleId(user.getRoleId());
                    userDbById.setStationList(user.getStationList()); //todo 以后做切换站功能需要
                    if (user.getActivated() != null) {
                        userDbById.setActivated(user.getActivated());
                    }
                    if (user.getDirectLoginKey() != null) {
                        userDbById.setDirectLoginKey(user.getDirectLoginKey());
                    }
                    userService.updateUser(userDbById);
                    return AjaxResult.success(entityToDto(userDbById, SOURCE_TYPE_OTHER), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_XGCG"));
                } else {
                    return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_BCZGYH"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("database error", e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_ZDIDBCZ"));
        } finally {
        }
    }

    /**
     * 查询用户
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = {"account/user"}, method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('account_user_r')")
    @ApiOperation(value = "查询用户接口", httpMethod = "GET", notes = "查询用户接口")
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        User userDb = userUtil.getCurrentUser();
        if (userDb == null) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_DQYHBCZ"));
        }
        UserRoleXref userRoleXrefDb = userRoleXrefService.getByUserId(userDb.getId());
        if (userRoleXrefDb != null) {
            userDb.setRoleId(userRoleXrefDb.getRoleId());
        }
        List<User> list = userService.list(whereRequest, userDb);
        List<UserDTO> dtoList = new ArrayList<>();
        if (list != null) {
            for (User u : list) {
                dtoList.add(entityToDto(u, SOURCE_TYPE_LIST));
            }
        }
        PageInfo<UserDTO> userPageInfo = new PageInfo(list);
        userPageInfo.setList(dtoList);
        return AjaxResult.success(userPageInfo, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_CXCG"));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"account/user/{id}"}, method = RequestMethod.DELETE)
//    @PreAuthorize("hasAuthority('account_user_d')")
    @ApiOperation(value = "删除用户接口", httpMethod = "DELETE", notes = "删除用户接口")
    @ResponseBody
    public AjaxResult delete(@PathVariable(value = "id") String id) {
        userService.fakeDeleteById(Long.valueOf(id));
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_SCCG"));
    }


//    @RequestMapping(value = {"account/user/role"}, method = RequestMethod.POST)
//    @ApiOperation(value = "用户角色绑定接口", httpMethod = "POST", notes = "用户角色绑定接口")
//    @ResponseBody
//    public AjaxResult bindRole(@RequestParam String userId, @RequestParam String roleId) {
//        User user = userService.bindRole(userId, roleId);
//        return AjaxResult.success(user, "绑定成功");
//    }

    /**
     * 正常登录
     *
     * @param userParam
     * @return
     */
    @RequestMapping(value = {"account/user/login"}, method = RequestMethod.POST)
    @ApiOperation(value = "登录接口", httpMethod = "POST", notes = "登录接口")
    @ResponseBody
    public AjaxResult login(@RequestBody User userParam) {
        if (StringUtil.isNullOrEmpty(userParam.getUserName()) || StringUtil.isNullOrEmpty(userParam.getPassword())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHMHMMWK"));
        }
        Map map = doLogin(userParam.getUserName(), userParam.getPassword(), Constant.RECORD_SCENE_SOURCE_PC);
        return doCheckLogin(map);
    }

    /**
     * 四位码快捷登录
     *
     * @param userParam
     * @return
     */
    @RequestMapping(value = {"account/user/login/pad"}, method = RequestMethod.POST)
    @ApiOperation(value = "PAD登录接口", httpMethod = "POST", notes = "PAD登录接口")
    @ResponseBody
    public AjaxResult directKeyLogin(@RequestBody User userParam) {
        try {
            if (userParam.getDirectLoginKey() == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_CSCW"));
            }
            Integer directLoginKey = userParam.getDirectLoginKey();
            //todo StoreId暂时用100去代替，以后用session里获取
            User userDb = userService.getUserByDirectKey(directLoginKey, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (userDb != null) {
                Map map = doLogin(userDb.getUserName(), userDb.getPassword(), Constant.RECORD_SCENE_SOURCE_PAD);
                return doCheckLoginPad(map);
            } else {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHBCZ"));
            }
        } catch (NumberFormatException e) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_CSCW"));
        } finally {
        }
    }

    /**
     * PC端的检查登录
     *
     * @param map
     * @return
     */
    private AjaxResult doCheckLogin(Map map) {
        UserDTO userDTO = (UserDTO) map.get("user");
        List<StationDTO4User> stationList = (List<StationDTO4User>) map.get("stationList");
        //如果拿到的user对象为空，则说明认证校验没有通过
        if (userDTO == null) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHMHMMCW"));
        }
        //如果用户是站管理员，绑定的站list为空或者是未激活的站，则登录失败
        if (userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))
                && (stationList == null || stationList.get(0).getActive() == Constant.DELETE)) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHWBDZQLXKF"));
        }
        //如果用户不是站管理员，走到这说明已经认证通过了，则直接登录成功
        if (!userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
            //写入枚举
            map.put("enums", getAllEnums(userDTO,localeMessageSourceService));
            map.put(VersionConstants.VERSION_NOAH_GOOR_SERVER_KEY, VersionConstants.VERSION_NOAH_GOOR_SERVER);
            //登录成功，初始化权限
//            initialUserPermission(userDTO.getId(), map);
            return AjaxResult.success(map, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_DLCG"));
        }
        //如果用户是站管理员，站list不是空，绑定的站也是激活的，则登录成功
        if (userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && stationList != null
                && stationList.get(0).getActive() == Constant.NORMAL) {
            //写入枚举
            map.put("enums", getAllEnums(userDTO,localeMessageSourceService));
            map.put(VersionConstants.VERSION_NOAH_GOOR_SERVER_KEY, VersionConstants.VERSION_NOAH_GOOR_SERVER);
            for (StationDTO4User stationDTO4User : stationList) {
                Integer stationTypeId = stationDTO4User.getStationTypeId();
                if (stationTypeId == null) {
                    LOGGER.info("##########用户绑定站:" + stationDTO4User.getName() + "的站类型为空");
                    continue;
                }
                if (stationTypeId.equals(StationType.ELEVATOR.getCaption())) {
                    // 用户绑定了电梯站
                    map.put(Constant.IS_BIND_ELEVATOR_STATION_FLAG, 1);
                    break;
                }
            }
            //登录成功，初始化权限
//            initialUserPermission(userDTO.getId(), map);
            return AjaxResult.success(map, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_DLCG"));
        } else {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHWBDZQLXKF"));
        }
    }

    /**
     * PAD端的检查登录
     *
     * @param map
     * @return
     */
    private AjaxResult doCheckLoginPad(Map map) {
        UserDTO userDTO = (UserDTO) map.get("user");
        List<StationDTO4User> stationList = (List<StationDTO4User>) map.get("stationList");
        //如果拿到的user对象为空，则说明认证校验没有通过
        if (userDTO == null) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHMHMMCW"));
        }
        //如果不是站管理员就不让其登录
        if (!userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_BSZGLYBNDL"));
        }
        //如果用户是站管理员，绑定的站list为空或者是未激活的站，则登录失败
        if (userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))
                && (stationList == null || stationList.get(0).getActive() == Constant.DELETE)) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHWBDZQLXKF"));
        }
        //如果用户是站管理员，站list不是空，绑定的站也是激活的，则登录成功
        if (userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && stationList != null
                && stationList.get(0).getActive() == Constant.NORMAL) {
            //写入枚举
            map.put("enums", getAllEnums(userDTO,localeMessageSourceService));
            map.put(VersionConstants.VERSION_NOAH_GOOR_SERVER_KEY, VersionConstants.VERSION_NOAH_GOOR_SERVER);
            OrderSetting defaultSetting = null;
            if (stationList.size() > 0) {
                StationDTO4User firstStation = stationList.get(0);
                defaultSetting = orderSettingService.getDefaultSetting(firstStation.getId());
            }
            map.put("orderSetting", defaultSetting);
            for (StationDTO4User stationDTO4User : stationList) {
                if (StationType.ELEVATOR.getCaption() == stationDTO4User.getStationTypeId()) {
                    // 用户绑定了电梯站
                    map.put(Constant.IS_BIND_ELEVATOR_STATION_FLAG, 1);
                    break;
                }
            }
            return AjaxResult.success(map, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_DLCG"));
        } else {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_YHWBDZQLXKF"));
        }
    }

    /**
     * 获取常量接口
     *
     * @return
     */
    @RequestMapping(value = {"getAllEnum"}, method = RequestMethod.GET)
    @ApiOperation(value = "获取常量接口", httpMethod = "GET", notes = "获取常量接口")
    @ResponseBody
    public AjaxResult getAllEnum() {
        Map map = Maps.newHashMap();
        map.put("enums", getAllEnums(null,localeMessageSourceService));
        return AjaxResult.success(map, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_CLCXCG"));
    }

    /**
     * 登录方法 todo 正常的有权限和菜单json和枚举的
     *
     * @param userName
     * @param password
     * @return
     */
    /*private User doLogin(String userName, String password) {
        //调auth_server的token接口
        try {
            String token = doAuthorize(userName, password);
            //判断token不等于null，说明已经登录
            if (token != null) {
                //查询用户的
                List<User> listAll = userService.getUser(userName, password);
                if (listAll != null) {
                    return listAll.get(0);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            log.error(e, e.getMessage());
        }
        return null;
    }*/

    /**
     * 注销接口
     *
     * @return
     */
    @RequestMapping(value = {"account/user/logOut"}, method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult logOut() {
        CacheInfoManager.removeUserLoginStatusCache(userUtil.getCurrentUser().getUserName());
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_ZXCG"));
    }

    /**
     * 用户绑定角色接口
     *
     * @param roleIds
     * @param userId
     * @return
     */
    @RequestMapping(value = {"account/user/bindRole"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_user_u')")
    @ApiOperation(value = "用户绑定角色接口", httpMethod = "POST", notes = "用户绑定角色接口")
    @ResponseBody
    public AjaxResult bindRole(@RequestParam(value = "roleIds") String roleIds, @RequestParam(value = "userId") Long userId) {
        try {
            //数据库查询相应角色是否存在
            User userDb = userService.findById(userId);
            if (userDb == null) {
                return AjaxResult.failed("用户不存在");
            }
            List<Long> roleIdList = JSON.parseArray(roleIds, Long.class);
            List<Role> roleList = roleService.listByIds(roleIdList);
            Set<Long> roleIdSet = Sets.newHashSet();
            roleList.forEach(role -> {
                roleIdSet.add(role.getId());
            });
            userService.bindRole(roleIdSet, userId);
        } catch (Exception e) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_GJSYBDGXWFSC"));
        }
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_user_controller_UserController_java_SCCG"));
    }


    /**
     * 登录方法
     *
     * @param userName
     * @param password
     * @return
     */
    private Map<String, Object> doLogin(String userName, String password, String source) {
        //调auth_server的token接口
        User user;
        Map map = new HashMap();
        try {
            String accessToken = doAuthorize(userName, password);
            //判断token不等于null，说明已经登录
            if (!StringUtil.isNullOrEmpty(accessToken)) {
                CacheInfoManager.setUserLoginStatusCache(userName, LOGGED_IN);
                //查询用户的
                List<User> list = userService.getUser(userName, password);
                if (list != null) {
                    user = list.get(0);
                    List<StationDTO4User> stationList = user.getStationList();
                    map.put("user", entityToDto(user, SOURCE_TYPE_OTHER));
                    map.put("access_token", accessToken);
                    if (stationList != null && stationList.size() > 0) {
                        //todo 暂时写死 临时添加场景id( ?? 注意对应 id 场景不存在的情况)
//                        session.setAttribute(Constant.SCENE_SESSION_TAG, new Scene(1L));
                        try {
                            this.sceneService.storeSceneInfoToSession(source, String.valueOf(stationList.get(0).getSceneId()), accessToken);
                        } catch (Exception e) {
                            LOGGER.info(" * * * * * * 指定 sceneId 编号的场景信息不存在. * * * * * * ");
                        }
                        map.put("stationList", stationList);
                    }
                    return map;
                }
            }
        } catch (Exception e) {
            LOGGER.error("errorMessage===>{}", e);
        } finally {
        }
        return map;
    }

    /**
     * 认证和权限校验，请求认证权限服务器
     *
     * @param username
     * @param pwd
     * @return
     * @throws Exception
     */
    private String doAuthorize(String username, String pwd) throws Exception {
        final String auth = "Basic " + new String(Base64.encodeBase64(new StringBuilder(Constant.AUTHORIZE_USERNAME + ":" + Constant.AUTHORIZE_PASSWORD).toString().getBytes()));
        Map params = Maps.newHashMap();
        params.put("username", username);
        params.put("password", pwd);
        params.put("grant_type", "password");
        String result = HttpClientUtil.executePost(null, "http://" + authServerHost + ":" + authServerPort + authServerApi, params, null, null, "application/x-www-form-urlencoded; charset=UTF-8", auth, "UTF-8", true);
        String accessToken = null;
        if (!StringUtil.isNullOrEmpty(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            accessToken = jsonObject.getString("access_token");
        }
        return accessToken;
    }

    /**
     * 根据调用方法的源头不一样来给stationList不同的值
     *
     * @param user
     * @param sourceType
     * @return
     */
    private UserDTO entityToDto(User user, int sourceType) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setRoleId(user.getRoleId());
        userDTO.setRoleName(user.getRoleName());
        userDTO.setActivated(user.getActivated());
        userDTO.setDirectLoginKey(user.getDirectLoginKey());
        List<StationDTO4User> stationDTO4UserList = new ArrayList<>();
        if (sourceType == SOURCE_TYPE_LIST) {
            if (user.getRoleId() != null && (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption())) || user.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption())))) {
                userDTO.setStationList(stationDTO4UserList);
            }
        } else {
            if (user.getRoleId() != null && user.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()))) {
                List<Station> stationList = stationService.list(null, user.getStoreId(), null);
                if (stationList != null && stationList.size() > 0) {
                    for (Station station : stationList) {
                        stationDTO4UserList.add(UserServiceImpl.stationToDTO(station));
                    }
                }
                userDTO.setStationList(stationDTO4UserList);
            }
            if (user.getRoleId() != null && user.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
                List<Station> stationList = stationService.list(null, null, null);
                if (stationList != null && stationList.size() > 0) {
                    for (Station station : stationList) {
                        stationDTO4UserList.add(UserServiceImpl.stationToDTO(station));
                    }
                }
                userDTO.setStationList(stationDTO4UserList);
            }
        }
        if (user.getRoleId() != null && user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
            userDTO.setStationList(user.getStationList());
        }
        return userDTO;
    }

    /**
     * 获取所有的枚举类
     *
     * @return
     */
    public final static Map getAllEnums(UserDTO userDTO,LocaleMessageSourceService localeMessageSourceService) {
        Map map = new HashMap();
        map.put("ICPointType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,IndustrialControlPointType.class,IndustrialControlPointType.values(),localeMessageSourceService));
        map.put("mapPointType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,MapPointType.class,MapPointType.values(),localeMessageSourceService));
        map.put("stationType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,StationType.class,StationType.values(),localeMessageSourceService));
        map.put("roleType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,RoleTypeEnum.class,RoleTypeEnum.values(),localeMessageSourceService));
        map.put("robotType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,RobotTypeEnum.class,RobotTypeEnum.values(),localeMessageSourceService));
        map.put("missionType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,MissionTypeEnum.class,MissionTypeEnum.values(),localeMessageSourceService));
        map.put("missionListType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,MissionListTypeEnum.class,MissionListTypeEnum.values(),localeMessageSourceService));
        map.put("moduleEnums", EnumUtil.enumListInternationalization(EnumUtil.MODULE_NAME,ModuleEnums.class,ModuleEnums.values(),localeMessageSourceService));
        map.put("rfidBraceletType", RfidBraceletTypeEnum.list());
        map.put("stateField", EnumUtil.enumListInternationalization(EnumUtil.CH_FIELD_NAME,StateFieldEnums.class,StateFieldEnums.values(),localeMessageSourceService));
        map.put("stationPointType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,MapPointType.class,Constant.STATION_MAP_POINT_TYPE_ARRAY,localeMessageSourceService));
        map.put("doorType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,DoorType.class,DoorType.values(),localeMessageSourceService));
        map.put("employeeType", EnumUtil.enumListInternationalization(EnumUtil.VALUE,EmployeeTypeEnum.class,EmployeeTypeEnum.values(),localeMessageSourceService));
        map.put("logLevel", EnumUtil.enumListInternationalization(EnumUtil.VALUE,LogLevel.class,LogLevel.values(),localeMessageSourceService));
        map.put("applianceDepartmentType", getApplianceDepartmentTypeList());
        map.put("appliancePackageType", getAppliancePackageTypeList());
        map.put("operationDepartmentType", getOperationDepartmentTypeList());

        //把当前用户能新建什么角色的用户放入常量返回前端
        List<RoleDTO> listNew = new ArrayList<>();
        if (userDTO != null) {
            if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
                Role role0 = new Role();
                role0.setId(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()));
                role0.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.SUPER_ADMIN.getValue()));
                Role role1 = new Role();
                role1.setId(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()));
                role1.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.HOSPITAL_ADMIN.getValue()));
                Role role2 = new Role();
                role2.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
                role2.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.STATION_ADMIN.getValue()));
                listNew.add(entityToDTO(role0));
                listNew.add(entityToDTO(role1));
                listNew.add(entityToDTO(role2));
                map.put("roleCreateLimit", listNew);
            } else if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()))) {
                Role role = new Role();
                role.setId(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()));
                role.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.HOSPITAL_ADMIN.getValue()));
                Role role1 = new Role();
                role1.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
                role1.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.STATION_ADMIN.getValue()));
                listNew.add(entityToDTO(role));
                listNew.add(entityToDTO(role1));
                map.put("roleCreateLimit", listNew);
            } else if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
                Role role1 = new Role();
                role1.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
                role1.setCnName(localeMessageSourceService.getMessage(RoleTypeEnum.STATION_ADMIN.getValue()));
                listNew.add(entityToDTO(role1));
                map.put("roleCreateLimit", listNew);
            } else {
                map.put("roleCreateLimit", null);
            }
        }
        return map;
    }

    /**
     * 角色实体转DTO
     *
     * @param role
     * @return
     */
    private static RoleDTO entityToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getCnName());
        return dto;
    }

    public static List<Map> getApplianceDepartmentTypeList() {
        List<Map> resultList = new ArrayList<Map>();
        ApplianceDepartmentTypeService applianceDepartmentTypeService = applicationContext.getBean(ApplianceDepartmentTypeService.class);
        List<ApplianceDepartmentType> departmentTypeList = applianceDepartmentTypeService.listAll();
        for (ApplianceDepartmentType departmentType : departmentTypeList) {
            Map result = new HashMap<String, Object>();
            result.put("name", departmentType.getName());
            result.put("value", departmentType.getCode());
            resultList.add(result);
        }
        return resultList;
    }

    private static List<Map> getAppliancePackageTypeList() {
        List<Map> resultList = new ArrayList<Map>();
        AppliancePackageTypeService appliancePackageTypeService = applicationContext.getBean(AppliancePackageTypeService.class);
        List<AppliancePackageType> packageTypeList = appliancePackageTypeService.listAllPackageType();
        for (AppliancePackageType packageType : packageTypeList) {
            Map result = new HashMap<String, Object>();
            result.put("name", packageType.getName());
            result.put("value", packageType.getId());
            resultList.add(result);
        }
        return resultList;
    }

    private static List<Map> getOperationDepartmentTypeList() {
        List<Map> resultList = new ArrayList<Map>();
        OperationDepartmentTypeService operationDepartmentTypeService = applicationContext.getBean(OperationDepartmentTypeService.class);
        List<OperationDepartmentType> departmentTypeList = operationDepartmentTypeService.listAllType();
        for (OperationDepartmentType departmentType : departmentTypeList) {
            Map result = new HashMap<String, Object>();
            result.put("name", departmentType.getName());
            result.put("value", departmentType.getId());
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserController.applicationContext = applicationContext;
    }

    /**
     * 登录后初始化用户权限
     *
     * @return
     */
    private void initialUserPermission(Long userId, Map map) {
        User userDb = userService.getById(userId);
        if (userDb == null) {
            return;
        }
        List<Permission> permissionList = userService.listAllPermission(userId);
        List<String> permissionUrlList = Lists.newArrayList();
        for (int i = 0; i < permissionList.size(); i++) {
            permissionUrlList.add(permissionList.get(i).getUrl());
        }
        map.put("allPermission", permissionUrlList);
    }

}
