package cn.muye.assets.shelf.service.impl;

import cn.mrobot.bean.assets.good.GoodType;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.shelf.mapper.ShelfMapper;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.BaseService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/22.
 */
@Service
@Transactional
public class ShelfServiceImpl extends BaseServiceImpl<Shelf> implements ShelfService {

    @SuppressWarnings({"unchecked", "deprecation"})
    @Autowired
    private ShelfMapper shelfMapper;

    public List<Shelf> listPageByStoreIdAndOrder(int page, int pageSize, String queryObj, Class<Shelf> clazz, String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(Shelf.class);
        Example.Criteria criteria = example.createCriteria();
        if (queryObj != null) {
            JSONObject jsonObject = JSONObject.parseObject(queryObj);
            String name = (String)jsonObject.get(SearchConstants.SEARCH_NAME);
            if (!StringUtil.isNullOrEmpty(name)) {
                criteria.andCondition("NAME like", "%" + name + "%");
            }
            String code = (String)jsonObject.get(SearchConstants.SEARCH_CODE);
            if (!StringUtil.isNullOrEmpty(code)) {
                criteria.andCondition("CODE =", code);
            }
        }
        example.setOrderByClause("ID DESC");
        List<Shelf> list = shelfMapper.selectByExample(example);
        for (Shelf shelf:list){
            //查询每一个货架所绑定的所有货物类别信息
            shelf.setGoodTypes(shelfMapper.findGoodsTypeByShelfId(shelf.getId()));
        }
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
        List<Long> goodsTypeIds = new ArrayList<>();
        int count =  shelfMapper.insert(shelf);
        if (shelf.getGoodTypes() != null && shelf.getGoodTypes().size() != 0) {
            //当存在对应绑定的货物类型的时候，保存类型对应关系
            for (GoodType goodType : shelf.getGoodTypes()) {
                goodsTypeIds.add(goodType.getId());
            }
            //保存货架与货物类型之间的多对多关系
            shelfMapper.insertShelfAndGoodsTypeRelations(shelf.getId(), goodsTypeIds);
        }
        return count;
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

    @Override
    public int insertShelfAndGoodsTypeRelations(Long shelfId, List<Long> goodsTypeIds) {
        return this.shelfMapper.insertShelfAndGoodsTypeRelations(shelfId, goodsTypeIds);
    }

    @Override
    public int updateByStoreId(Shelf entity) {
        int count = super.updateByStoreId(entity);
        shelfMapper.deleteHistoryRelations(entity.getId());
        if (entity.getGoodTypes() != null && entity.getGoodTypes().size() != 0) {
            List<Long> goodsTypeIds = new ArrayList<>();
            //当存在对应绑定的货物类型的时候，保存类型对应关系
            for (GoodType goodType : entity.getGoodTypes()) {
                goodsTypeIds.add(goodType.getId());
            }
            //保存货架与货物类型之间的多对多关系
            shelfMapper.insertShelfAndGoodsTypeRelations(entity.getId(), goodsTypeIds);
        }
        return count;
    }
}
