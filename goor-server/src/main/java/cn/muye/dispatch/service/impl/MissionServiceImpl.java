package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.Mission;
import cn.mrobot.bean.misssion.MissionMissionItemXREF;
import cn.mrobot.bean.misssion.MissionItem;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionMapper;
import cn.muye.dispatch.mapper.MissionMissionItemXREFMapper;
import cn.muye.dispatch.mapper.MissionItemMapper;
import cn.muye.dispatch.service.MissionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
@Service
@Transactional
public class MissionServiceImpl implements MissionService {

	@Autowired
	protected MissionMapper missionMapper;

	@Autowired
	protected MissionMissionItemXREFMapper missionMissionItemXREFMapper;

	@Autowired
	protected MissionItemMapper missionItemMapper;

	@Override
	public long save(Mission mission) {
		return missionMapper.save(mission);
	}

	@Override
	public void update(Mission mission) {
		missionMapper.update(mission);
	}

	@Override
	public void update(Mission mission, List<Long> nodeIdList) {
		Long missionChainId = mission.getId();
		MissionMissionItemXREF missionMissionItemXREF = new MissionMissionItemXREF();
		missionMissionItemXREF.setMissionChainId(missionChainId);
		//添加关联关系
		List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByChainId(missionChainId);
		for (int i = 0; i < nodeIdList.size(); i++) {
			missionMissionItemXREF.setMissionNodeId(nodeIdList.get(i));
			if (! missionMissionItemXREFList.contains(missionMissionItemXREF)) {
				missionMissionItemXREFMapper.save(missionMissionItemXREF);
			}
		}
		missionMapper.update(mission);
	}

	@Override
	public Mission get(long id) {
		return missionMapper.get(id);
	}

	@Override
	public void delete(Mission mission) {
		missionMissionItemXREFMapper.deleteByChainId(mission.getId());//删除关联关系
		missionMapper.delete(mission.getId());
	}

	@Override
	public Mission findByName(String name) {
		return missionMapper.findByName(name);
	}

	@Override
	public List<Mission> list(WhereRequest whereRequest) {
		List<Mission> missionList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object missionMainId = map.get(SearchConstants.SEARCH_MISSION_MAIN_ID);
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionList = missionMapper.list(missionMainId, name, beginDate, endDate, priority);
		}else {
			missionList = missionMapper.listAll();
		}
		return bindMissionNodeList(missionList);

	}

	@Override
	public List<Mission> list() {
		List<Mission> missionList = missionMapper.listAll();
		return bindMissionNodeList(missionList);
	}

	private List<Mission> bindMissionNodeList(List<Mission> missionList) {
		Mission mission;
		List<Mission> result = new ArrayList<>();
		for (int i = 0; i < missionList.size(); i++) {
			List<MissionItem> missionItemList = new ArrayList<>();
			mission = missionList.get(i);
			List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByChainId(mission.getId());
			for (int j = 0; j < missionMissionItemXREFList.size(); j++) {
				missionItemList.add(missionMissionItemXREFList.get(j).getMissionItem());
			}
			mission.setMissionItemList(missionItemList);
			result.add(mission);
		}
		return result;
	}

}

