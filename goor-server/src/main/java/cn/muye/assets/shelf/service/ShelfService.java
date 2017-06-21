package cn.muye.assets.shelf.service;

import cn.mrobot.bean.shelf.Shelf;
import cn.muye.assets.shelf.mapper.ShelfMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/20.
 */
@Service
@Transactional
public class ShelfService {

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

    public void save(Shelf shelf) {
        shelfMapper.insert(shelf);
    }

    public Shelf getById(Long id) {
        return shelfMapper.selectByPrimaryKey(id);
    }

    public void update(Shelf shelfDb) {
        shelfMapper.updateByPrimaryKey(shelfDb);
    }

    public void deleteById(Long id) {
        shelfMapper.deleteByPrimaryKey(id);
    }
}


