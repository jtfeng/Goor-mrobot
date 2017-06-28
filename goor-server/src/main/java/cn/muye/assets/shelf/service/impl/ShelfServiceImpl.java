package cn.muye.assets.shelf.service.impl;

import cn.mrobot.bean.assets.shelf.Shelf;
import cn.muye.assets.shelf.mapper.ShelfMapper;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.service.BaseService;
import cn.muye.base.service.imp.BaseServiceImpl;
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

    public List<Shelf> list() {
        Example example = new Example(Shelf.class);
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
