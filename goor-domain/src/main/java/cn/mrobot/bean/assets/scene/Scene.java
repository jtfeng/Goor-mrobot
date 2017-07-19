package cn.mrobot.bean.assets.scene;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by wlkfec on 19/07/2017.
 */
@Table(name = "AS_SCENE")
public class Scene extends BaseBean{
    private String name;
    private String aliasName;
    private String intro;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}