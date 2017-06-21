package cn.muye.dispatch.mapper;


import cn.mrobot.bean.misssion.MissionNode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
public interface MissionNodeMapper {

	long save(MissionNode missionNode);

	MissionNode findByName(String name);

	void update(MissionNode missionNode);

	void delete(MissionNode missionNode);

	MissionNode get(long id);

	List<MissionNode> list(@Param("missionChainId") Object missionChainId,
						   @Param("name") Object name,
						   @Param("beginDate") Object beginDate,
						   @Param("endDate") Object endDate,
						   @Param("priority") Object priority);

	List<MissionNode> listAll();
}

