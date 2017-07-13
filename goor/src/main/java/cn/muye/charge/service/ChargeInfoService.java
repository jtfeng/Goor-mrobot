package cn.muye.charge.service;


import cn.mrobot.bean.charge.ChargeInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/2
 * Time: 15:14
 * Describe:
 * Version:1.0
 */
public interface ChargeInfoService {

    void save(ChargeInfo chargeInfo);

    ChargeInfo get(Long id);

    void delete();

}
