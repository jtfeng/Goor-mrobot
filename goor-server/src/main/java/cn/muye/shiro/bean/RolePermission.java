package cn.muye.shiro.bean;

import java.io.Serializable;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public class RolePermission implements Serializable {

    private Long rId;

    private Long pId;

    public Long getrId() {
        return rId;
    }

    public void setrId(Long rId) {
        this.rId = rId;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }
}
