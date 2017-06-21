package cn.muye.dispatch.mapper;


import cn.mrobot.bean.misssion.MissionChain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/9
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
public interface MissionChainMapper {

	long save(MissionChain missionChain);

	void update(MissionChain missionChain);

	MissionChain get(long id);

	void delete(long id);

	MissionChain findByName(String name);

	List<MissionChain> list(@Param("missionMainId") Object missionMainId,
							@Param("name") Object name,
							@Param("beginDate") Object beginDate,
							@Param("endDate") Object endDate,
							@Param("priority") Object priority);

	List<MissionChain> listAll();
}

