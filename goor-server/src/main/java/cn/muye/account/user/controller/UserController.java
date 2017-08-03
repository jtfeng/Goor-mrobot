package cn.muye.account.user.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Role;
import cn.mrobot.bean.account.RoleTypeEnum;
import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserRoleXref;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.assets.rfidbracelet.RfidBraceletTypeEnum;
import cn.mrobot.bean.assets.robot.RobotTypeEnum;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.MissionListTypeEnum;
import cn.mrobot.bean.mission.MissionTypeEnum;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.bean.state.enums.StateFieldEnums;
import cn.mrobot.dto.account.RoleDTO;
import cn.mrobot.dto.account.UserDTO;
import cn.mrobot.dto.area.station.StationDTO4User;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.user.service.UserRoleXrefService;
import cn.muye.account.user.service.UserService;
import cn.muye.account.user.service.impl.UserServiceImpl;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/13.
 */
@Controller
public class UserController {

    private static Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StationService stationService;

    @Autowired
    private UserRoleXrefService userRoleXrefService;

    @Value("${authServer.host}")
    private String authServerHost;

    @Value("${authServer.port}")
    private String authServerPort;

    @Value("${authServer.api}")
    private String authServerApi;

    private static final int SOURCE_TYPE_LIST = 1; //列表来源

    private static final int SOURCE_TYPE_OTHER = 2; //其他来源

    @Autowired
    private UserUtil userUtil;

    /**
     * 新增修改用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = {"account/user"}, method = RequestMethod.POST)
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
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
            }
            if (userId == null && StringUtil.isNullOrEmpty(userName)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "用户名或密码不能为空");
            }
            if (roleId == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "角色不能为空");
            }
            /*
            if (user.getRoleId() != null && !user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && user.getStationList() != null && user.getStationList().size() > 0) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "不是站管理员角色，不能绑定站");
            }
            if (user.getRoleId() != null && user.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption())) && (user.getStationList() == null || user.getStationList().size() == 0)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站不能为空");
            }*/
            if (roleId != null && (stationList == null || (stationList != null && (stationList.size() > 1 || stationList.size() == 0)))) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站不能为空或者不能绑定多个站");
            }
            if (roleId != null && roleId.equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "不能新增超级管理员");
            }
            if (directLoginKey != null && directLoginKey > 9999 && directLoginKey < 1000) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "快捷密码必须是4位数");
            }
            User userDb = userService.getByUserName(userName);
            if (userDb != null && userDb.getUserName().equals(userName) && !userDb.getId().equals(user.getId())) {
                return AjaxResult.failed("用户名重复");
            }
            //todo StoreId暂时用100去代替，以后用session里获取
            if (directLoginKey != null) {
                User userDbByDirectKey = userService.getUserByDirectKey(directLoginKey, SearchConstants.FAKE_MERCHANT_STORE_ID);
                Long userDbByDirectKeyId = userDbByDirectKey.getId();
                Integer directLoginKeyDb = userDbByDirectKey.getDirectLoginKey();
                if (userDbByDirectKey != null && directLoginKey != null && directLoginKeyDb.equals(directLoginKey) && !userDbByDirectKeyId.equals(userId)) {
                    return AjaxResult.failed("4位快捷码重复");
                }
            }
            if (userId == null) {
                userService.addUser(user);
                return AjaxResult.success(entityToDto(user, SOURCE_TYPE_OTHER), "新增成功");
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
                    return AjaxResult.success(entityToDto(userDbById, SOURCE_TYPE_OTHER), "修改成功");
                } else {
                    return AjaxResult.failed("不存在该用户");
                }
            }
        } catch (Exception e) {
            LOGGER.error("database error", e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "站点ID不存在");
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
    @ApiOperation(value = "查询用户接口", httpMethod = "GET", notes = "查询用户接口")
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        User userDb = userUtil.getCurrentUser();
        if (userDb == null) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "当前用户不存在");
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
        return AjaxResult.success(userPageInfo, "查询成功");
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"account/user/{id}"}, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户接口", httpMethod = "DELETE", notes = "删除用户接口")
    @ResponseBody
    public AjaxResult delete(@PathVariable(value = "id") String id) {
        userService.fakeDeleteById(Long.valueOf(id));
        return AjaxResult.success("删除成功");
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
    public AjaxResult login(@RequestBody User userParam, HttpServletRequest request) {
        if (StringUtil.isNullOrEmpty(userParam.getUserName()) || StringUtil.isNullOrEmpty(userParam.getPassword())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "用户名或密码为空");
        }
        HttpSession session = request.getSession();
        Map map = doLogin(userParam.getUserName(), userParam.getPassword(), session);
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
    public AjaxResult directKeyLogin(@RequestBody User userParam, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            if (userParam.getDirectLoginKey() == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
            }
            Integer directLoginKey = userParam.getDirectLoginKey();
            //todo StoreId暂时用100去代替，以后用session里获取
            User userDb = userService.getUserByDirectKey(directLoginKey, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (userDb != null) {
                Map map = doLogin(userDb.getUserName(), userDb.getPassword(), session);
                return doCheckLogin(map);
            } else {
                return AjaxResult.failed("用户不存在");
            }
        } catch (NumberFormatException e) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
        } finally {
        }
    }

    private AjaxResult doCheckLogin(Map map) {
        UserDTO userDTO = (UserDTO) map.get("user");
        List<StationDTO4User> stationList = (List<StationDTO4User>) map.get("stationList");
        if (userDTO != null && stationList != null) {
            //写入枚举
            map.put("enums", getAllEnums(userDTO));
            return AjaxResult.success(map, "登录成功");
        } else if (userDTO != null && stationList == null){
            return AjaxResult.failed("账号异常，请联系客服");
        } else {
            return AjaxResult.failed("用户名或密码错误");
        }
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
                List<User> list = userService.getUser(userName, password);
                if (list != null) {
                    return list.get(0);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * 注销接口
     * @param request
     * @return
     */
    @RequestMapping(value = {"account/user/logOut"}, method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult logOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("access_token");
        return AjaxResult.success("注销成功");
    }


    /**
     * 登录方法
     *
     * @param userName
     * @param password
     * @return
     */
    private Map<String, Object> doLogin(String userName, String password, HttpSession session) {
        //调auth_server的token接口
        User user;
        Map map = new HashMap();
        try {
            String accessToken = doAuthorize(userName, password);
            //判断token不等于null，说明已经登录
            if (!StringUtil.isNullOrEmpty(accessToken)) {
                session.setAttribute("access_token", accessToken);
                //查询用户的
                List<User> list = userService.getUser(userName, password);
                if (list != null) {
                    user = list.get(0);
                    List<StationDTO4User> stationList = user.getStationList();
                    map.put("user", entityToDto(user, SOURCE_TYPE_OTHER));
                    map.put("access_token", accessToken);
                    if (stationList != null && stationList.size() > 0) {
                        session.setAttribute("stationId", user.getStationList().get(0).getId());
                        //todo 暂时写死 临时添加场景id
                        session.setAttribute(Constant.SCENE_SESSION_TAG, new Scene(1L));
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
        String result = HttpClientUtil.executePost(null, "http://" + authServerHost + ":" + authServerPort + authServerApi, params, null, null, "application/x-www-form-urlencoded; charset=UTF-8", auth,"UTF-8", true);
        String accessToken = null;
        if (!StringUtil.isNullOrEmpty(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            accessToken = jsonObject.getString("access_token");
        }
        return accessToken;
    }

    /**
     * 根据调用方法的源头不一样来给stationList不同的值
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
        List<StationDTO4User> stationDTO4UserList = new ArrayList<>();
        if (sourceType == SOURCE_TYPE_LIST) {
            if (user.getRoleId() != null && (user.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption())) || user.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))))  {
                userDTO.setStationList(stationDTO4UserList);
            }
        } else {
            if (user.getRoleId() != null && user.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption())))  {
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
    public final static Map getAllEnums(UserDTO userDTO) {
        Map map = new HashMap();
        map.put("mapPointType", MapPointType.list());
        map.put("stationType", StationType.list());
        map.put("roleType", RoleTypeEnum.list());
        map.put("robotType", RobotTypeEnum.list());
        map.put("missionType", MissionTypeEnum.list());
        //把当前用户能新建什么角色的用户放入常量返回前端
        List<RoleDTO> listNew = new ArrayList<>();
        if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()))) {
            Role role0 = new Role();
            role0.setId(Long.valueOf(RoleTypeEnum.SUPER_ADMIN.getCaption()));
            role0.setCnName(RoleTypeEnum.SUPER_ADMIN.getValue());
            Role role1 = new Role();
            role1.setId(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()));
            role1.setCnName(RoleTypeEnum.HOSPITAL_ADMIN.getValue());
            Role role2 = new Role();
            role2.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
            role2.setCnName(RoleTypeEnum.STATION_ADMIN.getValue());
            listNew.add(entityToDTO(role0));
            listNew.add(entityToDTO(role1));
            listNew.add(entityToDTO(role2));
            map.put("roleCreateLimit", listNew);
        } else if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()))){
            Role role = new Role();
            role.setId(Long.valueOf(RoleTypeEnum.HOSPITAL_ADMIN.getCaption()));
            role.setCnName(RoleTypeEnum.HOSPITAL_ADMIN.getValue());
            Role role1 = new Role();
            role1.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
            role1.setCnName(RoleTypeEnum.STATION_ADMIN.getValue());
            listNew.add(entityToDTO(role));
            listNew.add(entityToDTO(role1));
            map.put("roleCreateLimit", listNew);
        } else if (userDTO.getRoleId() != null && userDTO.getRoleId().equals(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()))) {
            Role role1 = new Role();
            role1.setId(Long.valueOf(RoleTypeEnum.STATION_ADMIN.getCaption()));
            role1.setCnName(RoleTypeEnum.STATION_ADMIN.getValue());
            listNew.add(entityToDTO(role1));
            map.put("roleCreateLimit", listNew);
        } else {
            map.put("roleCreateLimit", null);
        }
        return map;
    }

    /**
     * 角色实体转DTO
     * @param role
     * @return
     */
    private static RoleDTO entityToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getCnName());
        return dto;
    }
}
