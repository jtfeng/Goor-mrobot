package cn.muye.util;

import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserStationXref;
import cn.muye.account.user.service.UserStationXrefService;
import cn.muye.account.user.service.UserService;
import cn.muye.base.cache.CacheInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Selim on 2017/7/4.
 */
@Component
public class UserUtil {

    @Autowired
    private UserService userService;
    @Autowired
    private UserStationXrefService userStationXrefService;

    public  User getCurrentUser(HttpServletRequest request){
        if (request != null) {
            String accessToken = request.getHeader("access_token");
            String userName = CacheInfoManager.getLoggedUserCache(accessToken);
            User user = userService.getByUserName(userName);
            return user == null ? null : user;
        } else {
            return null;
        }
    }

    public  Long getCurrentUserId(HttpServletRequest request){
        return getCurrentUser(request) != null ? getCurrentUser(request).getId(): null;
    }

    public Long getStationId(HttpServletRequest request){
        if(getCurrentUser(request)!=null){
            List<UserStationXref> userStationXrefList = userStationXrefService.getByUserId(getCurrentUserId(request));
            if(userStationXrefList.size()> 0){
                return userStationXrefList.get(0).getStationId();
            }
        }
        return null;
    }

    /**
     * 获取当前用户的 token 值
     * @return
     */
    public static String getUserTokenValue(){
        if (SecurityContextHolder.getContext() == null ||
                SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
    }
}
