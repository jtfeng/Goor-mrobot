package cn.mrobot.dto.account;


import cn.mrobot.bean.area.station.Station;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/1.
 */
public class UserDTO {

    private Long id;

    private String userName; //用户名

    private Boolean activated; //是否启用

    private Integer directLoginKey; //快捷登陆口令（4位码）

    private Long roleId; //用户类型(1- 超级管理员， 2- 医院管理员, 3-站管理员)

    private List<Station> stationList; //站LIST

    private String roleName; //角色名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<Station> getStationList() {
        return stationList;
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
