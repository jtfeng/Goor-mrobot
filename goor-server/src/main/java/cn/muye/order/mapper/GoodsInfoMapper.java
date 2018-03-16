package cn.muye.order.mapper;

import cn.mrobot.bean.order.GoodsInfo;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Selim on 2017/7/8.
 */
public interface GoodsInfoMapper extends MyMapper<GoodsInfo> {
    List<GoodsInfo> listGoodsInfoByDetailId(@Param("orderDetailId")Long orderDetailId);
}
