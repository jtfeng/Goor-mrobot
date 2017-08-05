package cn.muye.util;

import cn.mrobot.bean.account.User;
import cn.mrobot.bean.account.UserStationXref;
import cn.muye.account.role.service.UserStationXrefService;
import cn.muye.account.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

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

    public  User getCurrentUser(){
        if (SecurityContextHolder.getContext() == null ||
                SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object object =  authentication.getPrincipal();
        User user = userService.getByUserName(object.toString());
        return user == null ? null : user;
    }

    public  Long getCurrentUserId(){
        return getCurrentUser() != null ? getCurrentUser().getId(): null;
    }

    public Long getStationId(){
        if(getCurrentUser()!=null){
            List<UserStationXref> userStationXrefList = userStationXrefService.getByUserId(getCurrentUserId());
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
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
    }
}
