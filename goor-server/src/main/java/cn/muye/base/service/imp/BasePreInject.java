package cn.muye.base.service.imp;

import cn.mrobot.bean.base.BaseBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Selim on 2017/7/7.
 */
@Service
public class BasePreInject<T extends BaseBean> {

    @Autowired
    private UserUtil userUtil;

    public T preInject(T entity, HttpServletRequest request){
        entity.setCreateTime(new Date());
        entity.setCreatedBy(userUtil.getCurrentUserId(request));
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return entity;
    }
}
