package cn.muye.shiro.mapper;

import cn.muye.shiro.bean.Role;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public interface RoleMapper {

    void save(Role role);

    void update(Role role);

    List<Role> listRole();

    Role getById(Long id);
}
