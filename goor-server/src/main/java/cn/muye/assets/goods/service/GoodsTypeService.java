package cn.muye.assets.goods.service;

import cn.mrobot.bean.assets.good.GoodsType;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.service.BaseService;

/**
 * Created by Selim on 2017/7/10.
 */
public interface GoodsTypeService extends BaseService<GoodsType> {

    void syncGoodsTypeMessage(MessageInfo messageInfo, String messageName);

}
