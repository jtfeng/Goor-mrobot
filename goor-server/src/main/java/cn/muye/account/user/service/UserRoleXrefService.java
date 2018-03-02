package cn.muye.account.user.service;

import cn.mrobot.bean.account.UserRoleXref;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface UserRoleXrefService extends BaseService<UserRoleXref> {

    List<UserRoleXref> list();

    UserRoleXref getByUserId(Long id);

    int save(UserRoleXref userRoleXref);

}
