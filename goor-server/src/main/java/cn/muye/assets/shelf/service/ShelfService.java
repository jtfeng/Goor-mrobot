package cn.muye.assets.shelf.service;

import cn.mrobot.bean.assets.shelf.Shelf;
import cn.muye.assets.shelf.mapper.ShelfMapper;
import cn.muye.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/20.
 */

public interface ShelfService extends BaseService<Shelf> {

    List<Shelf> listPageByStoreIdAndOrder(int page, int pageSize, String queryObj, Class<Shelf> clazz, String order);

    Shelf getByName(String name);

    Shelf getByCode(String code);

    int save(Shelf shelf);

    Shelf getById(Long id);

    int update(Shelf shelfDb);

    int deleteById(Long id);
}


