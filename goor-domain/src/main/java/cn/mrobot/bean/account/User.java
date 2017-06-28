package cn.mrobot.bean.account;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.*;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Table(name = "AC_USER")
public class User extends BaseBean{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName; //用户名

    private String password; //密码

    private Boolean activated; //是否启用

    private Integer directLoginKey; //快捷登陆口令（4位码）

    @Transient
    private Integer userType; //用户类型(1- 超级管理员， 2- 普通管理员)

    @Transient
    private String roleName; //角色名称

    @Transient
    private String accessToken; //登录验证码

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

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
