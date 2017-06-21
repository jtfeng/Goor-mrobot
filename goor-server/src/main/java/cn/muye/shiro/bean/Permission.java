package cn.muye.shiro.bean;

import java.io.Serializable;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public class Permission implements Serializable {

    private static final long serialVersionUID = 6069491220675321568L;

    private Long id;

    private String url;

    private String description;

    private String pattern;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
