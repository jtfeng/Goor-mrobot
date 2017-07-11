package cn.muye.log.charge.service.impl;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.log.charge.mapper.ChargeInfoMapper;
import cn.muye.log.charge.service.ChargeInfoService;
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
@Service
@Transactional
public class ChargeInfoServiceImpl  implements ChargeInfoService {

	@Autowired
	private ChargeInfoMapper chargingInfoMapper;

	public void save(ChargeInfo chargeInfo){
		chargingInfoMapper.save(chargeInfo);
	}

	public ChargeInfo get(Long id){
		return chargingInfoMapper.get(id);
	}

	public ChargeInfo getByDeviceId(String deviceId){
		return chargingInfoMapper.getByDeviceId(deviceId);
	}

	public List<ChargeInfo> lists(){
		return chargingInfoMapper.lists();
	}
}
