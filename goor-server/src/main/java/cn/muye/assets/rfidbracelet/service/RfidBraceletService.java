package cn.muye.assets.rfidbracelet.service;

import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by admin on 2017/7/3.
 */
public interface RfidBraceletService extends BaseService<RfidBracelet> {

    List<RfidBracelet> list();

    int save(RfidBracelet shelf);

    RfidBracelet getById(String id);

    int update(RfidBracelet rfidBracelet);

    int deleteById(Long id);

    List<RfidBracelet> listRfidBracelet(WhereRequest whereRequest);
}