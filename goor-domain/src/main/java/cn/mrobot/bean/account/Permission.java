package cn.mrobot.bean.account;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
@Table(name = "AC_PERMISSION")
public class Permission extends BaseBean {

    private String url; //接口URL

    private String description; //接口描述

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
