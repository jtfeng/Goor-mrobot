package cn.mrobot.dto.auth;

import java.io.Serializable;

/**
 * Created by admin on 2017/7/22.
 */
public class UserAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;

    public UserAuth() {

    }

    public UserAuth(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
