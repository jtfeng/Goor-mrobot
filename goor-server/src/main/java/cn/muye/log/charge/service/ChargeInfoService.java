package cn.muye.log.charge.service;

import cn.muye.log.charge.bean.ChargeInfo;
import cn.muye.log.charge.mapper.ChargeInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	ChargeInfo getByDeviceId(String deviceId);

	List<ChargeInfo> lists();
}
