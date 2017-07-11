package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionListMissionXREFMapper;
import cn.muye.dispatch.mapper.MissionListMapper;
import cn.muye.dispatch.mapper.MissionMapper;
import cn.muye.dispatch.mapper.MissionMissionItemXREFMapper;
import cn.muye.dispatch.service.MissionListService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class MissionListServiceImpl implements MissionListService {

	@Autowired
    protected MissionListMapper missionListMapper;

	@Autowired
	protected MissionMapper missionMapper;

	@Autowired
	protected MissionListMissionXREFMapper missionListMissionXREFMapper;

	@Autowired
	protected MissionMissionItemXREFMapper missionMissionItemXREFMapper;

	@Override
	public long save(MissionList missionList) {
		return missionListMapper.save(missionList);
	}

	@Override
	public void update(MissionList missionList) {
		missionListMapper.update(missionList);
	}

	@Override
	public void update(MissionList missionList, List<Long> missionIdList) {

		Long missionListId = missionList.getId();

		//添加关联关系,先全部删除，然后再关联
		missionListMissionXREFMapper.deleteByListId(missionListId);
		for (Long id : missionIdList) {
			//判断missionItem是否存在
			Mission mission = missionMapper.get(id);
			if(mission == null) {
				continue;
			}
			MissionListMissionXREF missionListMissionXREF = new MissionListMissionXREF();
			missionListMissionXREF.setMissionId(id);
			missionListMissionXREF.setMissionListId(missionListId);
			missionListMissionXREFMapper.save(missionListMissionXREF);
		}

		missionListMapper.update(missionList);
	}

//	@Override
//	public void update(MissionList missionList, List<Mission> missions) {
//		//添加关联关系
//		MissionListMissionXREF missionListMissionXREF = new MissionListMissionXREF();
//		missionListMissionXREF.setMissionListId(missionList.getId());
//		for (int i = 0; i < missions.size(); i++) {
//			missionListMissionXREF.setMissionId(missions.get(i).getId());
//			missionListMissionXREFMapper.save(missionListMissionXREF);
//		}
//		missionListMapper.update(missionList);
//	}

	@Override
    public MissionList get(long id) {
        return missionListMapper.get(id);
    }

	@Override
	public MissionList findByName(String name) {
		return missionListMapper.findByName(name);
	}

	@Override
	public void delete(MissionList missionList) {
		//删除关联关系
		missionListMissionXREFMapper.deleteByListId(missionList.getId());
		missionListMapper.delete(missionList.getId());
	}

	@Override
    public List<MissionList> list(WhereRequest whereRequest) {
		List<MissionList> missionListList = new ArrayList<>();
		if(whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
//			Object deviceId = map.get(SearchConstants.SEARCH_DEVICE_ID);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionListList = missionListMapper.list(name, /*deviceId,*/beginDate,endDate,priority);
		}

        return bindMission(missionListList);
    }

	@Override
	public List<MissionList> list() {
		return bindMission(missionListMapper.listAll());
	}

	private List<MissionList> bindMission(List<MissionList> missionLists){
		List<MissionList> result = new ArrayList<>();
		for(MissionList missionList : missionLists){
			List<Mission> missions = new ArrayList<Mission>();
			List<MissionListMissionXREF> missionListMissionXREFList = missionListMissionXREFMapper.findByListId(missionList.getId());
			for(MissionListMissionXREF missionListMissionXREF : missionListMissionXREFList){
				Mission mission = missionListMissionXREF.getMission();

				Set<MissionItem> missionItems = new HashSet<MissionItem>();
				List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByMissionId(mission.getId());
				for(MissionMissionItemXREF missionMissionItemXREF : missionMissionItemXREFList) {
					missionItems.add(missionMissionItemXREF.getMissionItem());
				}
				mission.setMissionItemSet(missionItems);
				missions.add(mission);
			}
			missionList.setMissionList(missions);
			result.add(missionList);
		}
		return result;
	}
}

