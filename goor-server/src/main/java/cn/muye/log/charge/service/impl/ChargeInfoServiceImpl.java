package cn.muye.log.charge.service.impl;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.log.charge.mapper.ChargeInfoMapper;
import cn.muye.log.charge.service.ChargeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
		chargingInfoMapper.insert(chargeInfo);
	}

	public ChargeInfo get(Long id){
		return chargingInfoMapper.selectByPrimaryKey(id);
	}

	public List<ChargeInfo> getByDeviceId(String deviceId){
		Example example = new Example(ChargeInfo.class);
		example.createCriteria().andCondition("DEVICE_ID=",deviceId);
		example.setOrderByClause("CREATE_TIME DESC");
		return chargingInfoMapper.selectByExample(example);
	}

	public List<ChargeInfo> lists(){
		return chargingInfoMapper.selectAll();
	}
}
