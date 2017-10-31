package cn.muye.log.charge.service;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.base.service.BaseService;

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
public interface ChargeInfoService extends BaseService<ChargeInfo> {

	ChargeInfo get(Long id);

	List<ChargeInfo> getByDeviceId(String deviceId);

	List<ChargeInfo> listAll();
}
