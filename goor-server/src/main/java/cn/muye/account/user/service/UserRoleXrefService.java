package cn.muye.account.user.service;

import cn.mrobot.bean.account.UserRoleXref;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface UserRoleXrefService {

    List<UserRoleXref> list();

    UserRoleXref getByUserId(Long id);

    void save(UserRoleXref userRoleXref);

    void update(UserRoleXref userRoleXref);
}
