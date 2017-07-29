package cn.mrobot.bean.account;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.dto.area.station.StationDTO4User;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Table(name = "AC_USER")
public class User extends BaseBean{

    private String userName; //用户名

    private String password; //密码

    private Boolean activated; //是否启用

    private Integer directLoginKey; //快捷登陆口令（4位码）

    private String accessToken; //临时解决方案(防止登陆后随时可能出现session过期问题)

    @Transient
    private Long roleId; //用户类型(1- 超级管理员， 2- 医院管理员, 3-站管理员)

    //    todo 以后还是要做切换站功能，暂时每个用户只能绑一个站，字段预留
    @Transient
    private List<StationDTO4User> stationList; //绑定的站List

    @Transient
    private Long storeId; //店铺ID

    @Transient
    private String roleName; //角色名称

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Integer getDirectLoginKey() {
        return directLoginKey;
    }

    public void setDirectLoginKey(Integer directLoginKey) {
        this.directLoginKey = directLoginKey;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<StationDTO4User> getStationList() {
        return stationList;
    }

    public void setStationList(List<StationDTO4User> stationList) {
        this.stationList = stationList;
    }

    @Override
    public Long getStoreId() {
        return storeId;
    }

    @Override
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
