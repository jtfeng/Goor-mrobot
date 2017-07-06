package cn.muye.base.service.imp;

import cn.mrobot.bean.base.BaseBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.BaseCrudService;
import cn.muye.util.MyMapper;
import cn.muye.util.UserUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Selim on 2017/6/23.
 */
@Service
public abstract class BaseCrudServiceImpl<T extends BaseBean>  implements BaseCrudService<T> {

    @Autowired
    protected MyMapper<T> myMapper;
    @Autowired
    private UserUtil userUtil;

    public int save(T entity) {
        entity.preSave();
        entity.setCreatedBy(userUtil.getCurrentUserId());
        entity.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.insert(entity);
    }

    public int delete(T entity) {
        return myMapper.delete(entity);
    }

    public int deleteById(Long id){
        return myMapper.deleteByPrimaryKey(id);
    }

    public int update(T entity) {
        return myMapper.updateByPrimaryKey(entity);
    }

    public int updateSelective(T entity) {
        return myMapper.updateByPrimaryKeySelective(entity);
    }

    public T findById(Long id) {
        return myMapper.selectByPrimaryKey(id);
    }

    public List<T> listAll() {
        return myMapper.selectAll();
    }

    public List<T> listPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return myMapper.select(null);
    }

}
