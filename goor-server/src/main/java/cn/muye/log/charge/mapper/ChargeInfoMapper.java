package cn.muye.log.charge.mapper;


import java.util.List;
import cn.mrobot.bean.charge.ChargeInfo;
/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/2
 * Time: 15:07
 * Describe:
 * Version:1.0
 */
public interface ChargeInfoMapper {

	long save(ChargeInfo chargeInfo);

	ChargeInfo get(Long id);

	ChargeInfo getByDeviceId(String deviceId);

	List<ChargeInfo> lists();

}
