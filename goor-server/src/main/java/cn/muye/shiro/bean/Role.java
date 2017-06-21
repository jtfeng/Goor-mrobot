package cn.muye.shiro.bean;

import java.io.Serializable;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public class Role implements Serializable {

    private static final long serialVersionUID = -2880285784522218056L;

    private Long id;

    private String enName;

    private String cnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }
}
