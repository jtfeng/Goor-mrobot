package cn.muye.order.service;

import cn.mrobot.bean.order.MessageBell;
import cn.muye.base.service.BaseService;

import java.util.Date;

/**
 * Created by Selim on 2017/10/10.
 */
public interface MessageBellService extends BaseService<MessageBell>{
    void updateByStationIdAndClearDate(Long stationId, Date clearDate);

    MessageBell findByMissionItemId(Long id);
}
