package cn.muye.base.service.imp;

import cn.mrobot.bean.base.BaseBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Selim on 2017/7/7.
 */
@Service
public class BasePreInject<T extends BaseBean> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserUtil userUtil;

    public T preInject(T entity){
        entity.setCreateTime(new Date());
        entity.setCreatedBy(userUtil.getCurrentUserId());
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return entity;
    }
}
