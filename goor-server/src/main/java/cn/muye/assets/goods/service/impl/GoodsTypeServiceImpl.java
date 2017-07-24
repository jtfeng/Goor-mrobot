package cn.muye.assets.goods.service.impl;

import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.GoodsService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Selim on 2017/7/10.
 */
@Service
@Transactional
public class GoodsTypeServiceImpl extends BaseServiceImpl<GoodsType> implements GoodsTypeService {
    private static final String GOODS_TYPE_DATA = "goods_type_data";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private GoodsService goodsService;
    @Override
    public void syncGoodsTypeMessage(MessageInfo messageInfo, String messageName) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_SUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String,String>(){{
            put(TopicConstants.PUB_NAME, messageName);
            List<GoodsType> goodsTypeList = listAll();
            goodsTypeList.forEach(goodsType -> goodsType.setGoodsList(goodsService.listGoodsByType(goodsType.getId())));
            put(GOODS_TYPE_DATA, JSON.toJSONString(goodsTypeList));
        }}))));
        MessageInfo info = new MessageInfo();
        info.setUuId(messageInfo.getUuId());
        info.setSendTime(new Date());
        info.setSenderId(messageInfo.getReceiverId());
        info.setReceiverId(messageInfo.getSenderId());
        info.setMessageType(MessageType.EXECUTOR_COMMAND);
        info.setMessageText(JSON.toJSONString(commonInfo));
        String noResultCommandRoutingKey = RabbitMqBean.getRoutingKey(messageInfo.getSenderId(),
                false, MessageType.EXECUTOR_COMMAND.name());
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultCommandRoutingKey, info);
    }
}
