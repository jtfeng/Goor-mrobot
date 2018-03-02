package cn.muye.account.user.service;

import cn.mrobot.bean.account.Permission;
import cn.mrobot.bean.account.User;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Set;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
public interface UserService extends BaseService<User> {

    List<User> getUser(String userName, String password) throws Exception;

    void addUser(User user);

    void updateUser(User user);

    List<User> list(WhereRequest whereRequest, User user) ;

    User getUserByDirectKey(Integer directKey, Long storeId);

    User getByUserName(String username);

    User getById(Long id);

    void fakeDeleteById(Long id);

    void bindRole(Set<Long> roleSet, Long userId);

    List<Permission> listAllPermission(Long userId);
}
