package cn.muye.log.charge.service.impl;

import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.base.service.imp.BaseServiceImpl;
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
public class ChargeInfoServiceImpl extends BaseServiceImpl<ChargeInfo> implements ChargeInfoService {

	@Autowired
	private ChargeInfoMapper chargingInfoMapper;

	@Override
	public ChargeInfo get(Long id){
		return chargingInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<ChargeInfo> getByDeviceId(String deviceId){
		Example example = new Example(ChargeInfo.class);
		example.createCriteria().andCondition("DEVICE_ID=",deviceId);
		example.setOrderByClause("CREATE_TIME DESC");
		return chargingInfoMapper.selectByExample(example);
	}

	@Override
	public List<ChargeInfo> listAll(){
		return chargingInfoMapper.selectAll();
	}
}
