package cn.muye.charge.mapper;

import cn.mrobot.bean.charge.ChargeInfo;

import java.util.List;

/**
 * Created by Jelynn on 2017/7/11.
 */
public interface ChargeInfoMapper {

    long save(ChargeInfo chargeInfo);

    List<ChargeInfo> list();

    void delete();
}
