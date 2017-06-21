package cn.muye.shiro.bean;

import java.io.Serializable;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public class UserRole implements Serializable {

    private static final long serialVersionUID = -2339027706082505730L;

    private Long uId;

    private Long rId;

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

    public Long getrId() {
        return rId;
    }

    public void setrId(Long rId) {
        this.rId = rId;
    }
}
