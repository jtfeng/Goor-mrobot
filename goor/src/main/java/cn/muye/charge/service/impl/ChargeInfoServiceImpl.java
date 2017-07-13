package cn.muye.charge.service.impl;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.charge.mapper.ChargeInfoMapper;
import cn.muye.charge.service.ChargeInfoService;
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
public class ChargeInfoServiceImpl implements ChargeInfoService {

	@Autowired
	private ChargeInfoMapper chargingInfoMapper;

	public void save(ChargeInfo chargeInfo){
		chargingInfoMapper.save(chargeInfo);
	}

	public ChargeInfo get(Long id){
		List<ChargeInfo> chargeInfoList = chargingInfoMapper.list();
		if(chargeInfoList.size() > 0 ){
			return chargeInfoList.get(0);
		}
		return null;
	}

	@Override
	public void delete() {
		chargingInfoMapper.delete();
	}
}
