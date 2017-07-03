package cn.muye.account.user.controller;

import cn.mrobot.bean.account.RoleTypeEnum;
import cn.mrobot.bean.account.User;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.assets.robot.RobotTypeEnum;
import cn.mrobot.dto.account.UserDTO;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.user.service.UserService;
import cn.muye.base.bean.AjaxResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import cn.mrobot.bean.constant.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/13.
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    private static HttpClient httpClient = new HttpClient();

    @Value("${authServer.host}")
    private String authServerHost;

    @Value("${authServer.port}")
    private String authServerPort;

    @Value("${authServer.api}")
    private String authServerApi;

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
        if (user == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
        }
        if (StringUtil.isNullOrEmpty(user.getUserName()) || StringUtil.isNullOrEmpty(user.getPassword())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "用户名或密码不能为空");
        }
        if (user.getRoleId() == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "角色不能为空");
        }
        if (user.getRoleId() != null && user.getRoleId().equals(3L) && user.getStationList() == null && user.getStationList().size() == 0) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站不能为空");
        }
        if (user.getRoleId() != null && user.getRoleId().equals(1L)) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "不能新增超级管理员");
        }
        User userDb = userService.getByUserName(user.getUserName());
        if (userDb != null && userDb.getUserName().equals(user.getUserName()) && !userDb.getId().equals(user.getId())) {
            return AjaxResult.failed("用户名重复");
        }
        User userDbByDirectKey = userService.getUserByDirectKey(user.getDirectLoginKey());
        if (userDbByDirectKey != null && userDbByDirectKey.getDirectLoginKey() != null && userDbByDirectKey.getDirectLoginKey().equals(user.getDirectLoginKey()) && !userDbByDirectKey.getId().equals(user.getId())) {
            return AjaxResult.failed("4位快捷码重复");
        }
        Long id = user.getId();
        if (id == null) {
            userService.addUser(user);
            return AjaxResult.success(entityToDto(user), "新增成功");
        } else {
            User userDbById = userService.getById(id);
            if (userDbById != null) {
                userDbById.setUserName(user.getUserName());
                userDbById.setPassword(user.getPassword());
                userDbById.setRoleId(user.getRoleId());
                userDbById.setStationList(user.getStationList());
                if (user.getActivated() != null) {
                    userDbById.setActivated(user.getActivated());
                }
                userDbById.setDirectLoginKey(user.getDirectLoginKey());
                userService.updateUser(userDbById);
                return AjaxResult.success(entityToDto(userDbById), "修改成功");
            } else {
                return AjaxResult.failed("不存在该用户");
            }
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
        List<User> list = userService.list(whereRequest);
        List<UserDTO> dtoList = new ArrayList<>();
        if (list != null) {
            for (User u : list) {
                dtoList.add(entityToDto(u));
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
    public AjaxResult login(@RequestBody User userParam, HttpServletResponse httpResponse) {
        if (StringUtil.isNullOrEmpty(userParam.getUserName()) || StringUtil.isNullOrEmpty(userParam.getPassword())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "用户名或密码为空");
        }
        Map map = doLogin(userParam.getUserName(), userParam.getPassword(), httpResponse);
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
    public AjaxResult directKeyLogin(@RequestBody User userParam, HttpServletResponse response) {
        try {
            if (userParam.getDirectLoginKey() == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
            }
            Integer directLoginKey = userParam.getDirectLoginKey();
            User userDb = userService.getUserByDirectKey(directLoginKey);
            if (userDb != null) {
                Map map = doLogin(userDb.getUserName(), userDb.getPassword(), response);
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
        User user = (User) map.get("user");
        if (user != null) {
            //写入枚举
            map.put("enums", getAllEnums());
            return AjaxResult.success(map, "登录成功");
        } else {
            return AjaxResult.failed("登录失败");
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
     * 登录方法
     *
     * @param userName
     * @param password
     * @return
     */
    private Map<String, Object> doLogin(String userName, String password, HttpServletResponse response) {
        //调auth_server的token接口
        User user = null;
        Map map = new HashMap();
        try {
            String accessToken = doAuthorize(userName, password);
            //判断token不等于null，说明已经登录
            if (!StringUtil.isNullOrEmpty(accessToken)) {
                //查询用户的
                List<User> list = userService.getUser(userName, password);
                if (list != null) {
                    user = list.get(0);
                    map.put("user", user);
                    response.setHeader("access_token", accessToken);
                    return map;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
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
        HttpClientParams httpParams = new HttpClientParams();
        httpParams.setSoTimeout(30000);
        httpClient.setParams(httpParams);
        httpClient.getHostConfiguration().setHost(authServerHost, Integer.valueOf(authServerPort));
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        PostMethod login = new PostMethod(authServerApi);
        login.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        final String auth = "Basic " + new String(Base64.encodeBase64(new StringBuilder(Constant.AUTHORIZE_USERNAME + ":" + Constant.AUTHORIZE_PASSWORD).toString().getBytes()));
        login.addRequestHeader("Authorization", auth);
        NameValuePair userName = new NameValuePair("username", username);// 邮箱
        NameValuePair password = new NameValuePair("password", pwd);// 密码
        NameValuePair grantType = new NameValuePair("grant_type", "password");// 密码

        NameValuePair[] data = {userName, password, grantType};
        login.setRequestBody(data);
        httpClient.executeMethod(login);
        int statusCode = login.getStatusCode();
        if (statusCode == 200) {
            String result = login.getResponseBodyAsString();
            JSONObject jsonObject = (JSONObject) JSON.parse(result);
            String accessToken = (String) jsonObject.get("access_token");
            return accessToken;
        } else {
            return null;
        }
    }

    private UserDTO entityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setRoleId(user.getRoleId());
        userDTO.setRoleName(user.getRoleName());
        userDTO.setDirectLoginKey(user.getDirectLoginKey());
        userDTO.setActivated(user.getActivated());
        userDTO.setStationList(user.getStationList());
        return userDTO;
    }

    /**
     * 获取所有的枚举类
     *
     * @return
     */
    public final static Map getAllEnums() {
        Map map = new HashMap();
        map.put("mapPointType", MapPointType.list());
        map.put("stationType", StationType.list());
        map.put("roleType", RoleTypeEnum.list());
        map.put("robotType", RobotTypeEnum.list());
        return map;
    }
}
