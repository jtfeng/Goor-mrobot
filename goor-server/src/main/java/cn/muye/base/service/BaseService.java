package cn.muye.base.service;

import cn.mrobot.bean.base.BaseBean;

import java.util.List;

/**
 * Created by Selim on 2017/6/23.
 */
public interface BaseService<T extends BaseBean> extends BaseCrudService<T> {

    //判定当前storeId的 RU
    int updateByStoreId(T entity);

    int updateSelectiveByStoreId(T entity);

    List<T> listAllByStoreId(Class<T> clazz);

    List<T> listPageByStoreIdAndOrder(int page, int pageSize, Class<T> clazz, String order);


}
