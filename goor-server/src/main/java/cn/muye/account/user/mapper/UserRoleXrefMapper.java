package cn.muye.account.user.mapper;

import cn.mrobot.bean.account.UserRoleXref;
import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/27.
 */
public interface UserRoleXrefMapper extends MyMapper<UserRoleXref> {

    List<UserRoleXref> list();

    UserRoleXref getByUserId(Long id);

}
