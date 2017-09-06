package cn.muye.base.service;

import cn.mrobot.bean.base.BaseBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Selim on 2017/6/23.
 */
public interface BaseCrudService<T extends BaseBean> {

    int save(T entity);

    int delete(T entity);

    int deleteById(Long id);

    int update(T entity);

    int updateSelective(T entity);

    T findById(Long id);

    List<T> listAll();

    List<T> listPage(int page, int pageSize);


}
