package cn.muye.assets.shelf.service.impl;

import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.shelf.mapper.ShelfMapper;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.BaseService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Service
@Transactional
public class ShelfServiceImpl extends BaseServiceImpl<Shelf> implements ShelfService {

    @Autowired
    private ShelfMapper shelfMapper;

    public List<Shelf> listPageByStoreIdAndOrder(int page, int pageSize, String queryObj, Class<Shelf> clazz, String order) {
        Example example = new Example(Shelf.class);
        Example.Criteria criteria = example.createCriteria();
        JSONObject jsonObject = JSONObject.parseObject(queryObj);
        String name = (String)jsonObject.get(SearchConstants.SEARCH_NAME);
        if (!StringUtil.isNullOrEmpty(name)) {
            criteria.andCondition("NAME like", "%" + name + "%");
        }
        String code = (String)jsonObject.get(SearchConstants.SEARCH_CODE);
        if (!StringUtil.isNullOrEmpty(code)) {
            criteria.andCondition("CODE =", code);
        }
        example.setOrderByClause("ID DESC");
        List<Shelf> list = shelfMapper.selectByExample(example);
        return list;
    }

    public Shelf getByName(String name) {
        Shelf shelf = new Shelf();
        shelf.setName(name);
        return shelfMapper.selectOne(shelf);
    }

    public Shelf getByCode(String code) {
        Shelf shelf = new Shelf();
        shelf.setCode(code);
        return shelfMapper.selectOne(shelf);
    }

    public int save(Shelf shelf) {
        return shelfMapper.insert(shelf);
    }

    public Shelf getById(Long id) {
        return shelfMapper.selectByPrimaryKey(id);
    }

    public int update(Shelf shelfDb) {
        return shelfMapper.updateByPrimaryKey(shelfDb);
    }

    public int deleteById(Long id) {
        return shelfMapper.deleteByPrimaryKey(id);
    }
}
