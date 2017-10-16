package cn.muye.base.service.imp;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.Underline2Camel;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.BaseService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Selim on 2017/6/23.
 */
@Service
public abstract class BaseServiceImpl<T extends BaseBean>  extends BaseCrudServiceImpl<T> implements BaseService<T> {

    public int updateByStoreId(T entity) {
        Example example = new Example(entity.getClass());
        example.createCriteria()
                .andCondition("ID =", entity.getId())
                .andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.updateByExample(entity, example);
    }

    public int updateSelectiveByStoreId(T entity) {
        Example example = new Example(entity.getClass());
        example.createCriteria()
                .andCondition("ID =", entity.getId())
                .andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.updateByExampleSelective(entity, example);
    }

    public List<T> listAllByStoreId(Class<T> clazz) {
        Example example = new Example(clazz);
        example.createCriteria().andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        return myMapper.selectByExample(example);
    }

    public List<T> listPageByStoreIdAndOrder(int page, int pageSize, Class<T> clazz, String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(clazz);
        example.createCriteria().andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if(!StringUtil.isNullOrEmpty(order)){
            example.setOrderByClause(order);
        }
        return myMapper.selectByExample(example);
    }

    public List<T> listQueryPageByStoreIdAndOrder(Integer page, Integer pageSize, T entity, String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(getFieldValueByName(field.getName(), entity)!=null){
                criteria.andCondition(Underline2Camel.camel2Underline(field.getName())+ "=", getFieldValueByName(field.getName(), entity));
            }
        }
        criteria.andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if(!StringUtil.isNullOrEmpty(order)){
            example.setOrderByClause(order);
        }
        return myMapper.selectByExample(example);
    }

    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }


}
