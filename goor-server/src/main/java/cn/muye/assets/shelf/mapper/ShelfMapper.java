package cn.muye.assets.shelf.mapper;

import cn.mrobot.bean.assets.good.GoodType;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/20.
 */
public interface ShelfMapper extends MyMapper<Shelf> {

    /**
     * 多对多关系插入货架与绑定的货物类型的关系
     * @param shelfId       货架 ID 编号
     * @param goodsTypeIds  可装载货物类型编号
     * @return  数据更新的记录数
     */
    int insertShelfAndGoodsTypeRelations(Long shelfId, List<Long> goodsTypeIds);

    /**
     * 根据货架 ID 编号查找绑定的所有货物类别信息
     * @param shelfId
     * @return
     */
    List<GoodType> findGoodsTypeByShelfId(Long shelfId);

    /**
     * 根据货架编号删除旧的多对多对应关系
     * @param shelfId
     */
    void deleteHistoryRelations(Long shelfId);
}
