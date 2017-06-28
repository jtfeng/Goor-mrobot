package cn.muye.account.user.mapper;

import cn.mrobot.bean.account.User;
import cn.muye.util.MyMapper;

/**
 * Created by Ray.Fu on 2017/6/13.
 */
public interface UserMapper extends MyMapper<User> {

    User getByUserName(String username);

    void fakeDeleteById(Long id);

    User getUserByDirectKey(Integer directKey);
}
