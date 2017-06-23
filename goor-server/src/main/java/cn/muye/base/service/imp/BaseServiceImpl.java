package cn.muye.base.service.imp;

import cn.mrobot.bean.base.BaseBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.BaseService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Selim on 2017/6/23.
 */
@Service
public abstract class BaseServiceImpl<T extends BaseBean>  extends BaseCrudServiceImpl<T> implements BaseService<T> {

    //关系到storeId
    public int deleteByStoreId(T entity) {
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.delete(entity);
    }

    public int updateByStoreId(T entity) {
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.updateByPrimaryKey(entity);
    }

    public int updateSelectiveByStoreId(T entity) {
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.updateByPrimaryKeySelective(entity);
    }

    public List<T> listAllByStoreId() {
        Example example = new Example(myMapper.getClass());
        example.createCriteria().andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.selectByExample(example);
    }

    public List<T> listPageByStoreId(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(myMapper.getClass());
        example.createCriteria().andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.selectByExample(example);
    }
}
