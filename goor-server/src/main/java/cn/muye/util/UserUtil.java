package cn.muye.util;

import cn.mrobot.bean.account.User;
import cn.muye.account.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by Selim on 2017/7/4.
 */
@Component
public class UserUtil {

    @Autowired
    private UserService userService;

    public  User getCurrentUser(){
        if (SecurityContextHolder.getContext() == null ||
                SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return null;
        }
        Object object =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByUserName(object.toString());
        return user == null ? null : user;
    }

    public  Long getCurrentUserId(){
        return getCurrentUser() != null ? getCurrentUser().getId(): null;
    }
}
