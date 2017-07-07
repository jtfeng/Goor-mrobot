package cn.mrobot.bean.assets.rfidbracelet;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by admin on 2017/7/3.
 */

/**
 * 定义手环基本信息实体
 */
@Table(name = "AS_RFIDBRACELET")
public class RfidBracelet extends BaseBean {

    @Column(name = "BRACELET_ID", length = 50)
    private String bracblbtId;
    @Column(name = "BRACELET_NAME", length = 50)
    private String bracblbtName;
    @Column(name = "BRACELET_USERID", length = 36)
    private String bracblbtUserid;
    @Column(name = "BRACELET_USERNAME", length = 50)
    private String bracblbtUsername;
    @Column(name = "BRACELET_AUTH")
    private Integer bracblbtAuth;

    public String getBracblbtId() {
        return bracblbtId;
    }

    public void setBracblbtId(String bracblbtId) {
        this.bracblbtId = bracblbtId;
    }

    public String getBracblbtName() {
        return bracblbtName;
    }

    public void setBracblbtName(String bracblbtName) {
        this.bracblbtName = bracblbtName;
    }

    public String getBracblbtUserid() {
        return bracblbtUserid;
    }

    public void setBracblbtUserid(String bracblbtUserid) {
        this.bracblbtUserid = bracblbtUserid;
    }

    public String getBracblbtUsername() {
        return bracblbtUsername;
    }

    public void setBracblbtUsername(String bracblbtUsername) {
        this.bracblbtUsername = bracblbtUsername;
    }

    public Integer getBracblbtAuth() {
        return bracblbtAuth;
    }

    public void setBracblbtAuth(Integer bracblbtAuth) {
        this.bracblbtAuth = bracblbtAuth;
    }
}