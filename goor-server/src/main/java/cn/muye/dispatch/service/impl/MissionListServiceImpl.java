package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionListMissionXREFMapper;
import cn.muye.dispatch.mapper.MissionListMapper;
import cn.muye.dispatch.mapper.MissionMapper;
import cn.muye.dispatch.mapper.MissionMissionItemXREFMapper;
import cn.muye.dispatch.service.MissionListService;
import cn.muye.dispatch.service.MissionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

	@Autowired
	protected MissionService missionService;

	@Override
	public long save(MissionList missionList) {
		return missionListMapper.save(missionList);
	}

	@Override
	public void update(MissionList missionList) {
		missionListMapper.update(missionList);
	}

	@Override
	public void update(MissionList missionList, List<Long> missionIdList,long storeId) {

		Long missionListId = missionList.getId();

		//添加关联关系,先全部删除，然后再关联
		if(missionListId == null) {
			missionList.setStoreId(storeId);
			missionListMapper.save(missionList);
			missionListId = missionList.getId();
		}
		else {
			missionListMapper.update(missionList);
			missionListMissionXREFMapper.deleteByListId(missionListId);
		}

		for (Long id : missionIdList) {
			//判断missionItem是否存在
			Mission mission = missionMapper.get(id,storeId);
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
    public MissionList get(long id,long storeId) {
		MissionList missionList = missionListMapper.get(id,storeId);
		if(missionList != null) {
			List<MissionList> missionLists = new ArrayList<MissionList>();
			missionLists.add(missionList);
			bindMission(missionLists);
			missionList = missionLists.get(0);
		}
        return missionList;
    }

	@Override
	public MissionList findByName(String name,long storeId) {
		return missionListMapper.findByName(name,storeId);
	}

	@Override
	public void delete(MissionList missionList,long storeId) {
		//删除关联关系
		missionListMissionXREFMapper.deleteByListId(missionList.getId());
		missionListMapper.delete(missionList.getId(),storeId);
	}

	@Override
    public List<MissionList> list(WhereRequest whereRequest,long storeId) {
		List<MissionList> missionListList = new ArrayList<>();
		if(whereRequest != null && whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
//			Object deviceId = map.get(SearchConstants.SEARCH_DEVICE_ID);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);
			Object sceneId = map.get(SearchConstants.SEARCH_SCENE_ID);
			Object missionListType = map.get(SearchConstants.SEARCH_MISSION_LIST_TYPE);

			missionListList = missionListMapper.list(name, /*deviceId,*/beginDate,endDate,priority,storeId,sceneId,missionListType);
		}else {
			missionListList = missionListMapper.listAll(storeId);
		}

        return bindMission(missionListList);
    }

	@Override
	public List<MissionList> list(long storeId) {
		return bindMission(missionListMapper.listAll(storeId));
	}

	//查找出关联的任务
	private List<MissionList> bindMission(List<MissionList> missionLists){
		for(MissionList missionList : missionLists){
			List<Mission> missions = new ArrayList<Mission>();
			List<MissionListMissionXREF> missionListMissionXREFList = missionListMissionXREFMapper.findByListId(missionList.getId(),null);
			for(MissionListMissionXREF missionListMissionXREF : missionListMissionXREFList){
				Mission mission = missionListMissionXREF.getMission();
				if(mission == null) {
					continue;
				}

				Set<MissionItem> missionItems = new HashSet<MissionItem>();
				List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByMissionId(mission.getId());
				for(MissionMissionItemXREF missionMissionItemXREF : missionMissionItemXREFList) {
					missionItems.add(missionMissionItemXREF.getMissionItem());
				}
				mission.setMissionItemSet(missionItems);
				missions.add(mission);
			}
			missionList.setMissionList(missions);
		}
		return missionLists;
	}

	public void updateFull(MissionList missionList, MissionList missionListDB,long storeId) {
		Date now = new Date();
		if(missionListDB == null) {
			missionList.setCreateTime(now);
		}
		//新建关联任务，并更新关联关系
		List<Mission> missions = missionList.getMissionList();
		List<Long> bindList = new ArrayList<Long>();
		if(missions != null && missions.size() > 0) {
			for(Mission mission : missions) {
				//如果mission名称为空，则取父级的名称加时间戳
				String name = mission.getName();
				if(name == null || name.equals("")) {
					name = missionList.getName();
				}
				mission.setName(name + "_" + DateTimeUtils.getCurrentDateTimeString());
				if (mission.getId() != null) {
					Mission missionDB = missionService.get(mission.getId(),storeId);
					//有id且数据库不存在的任务不做处理
					if(missionDB == null) {
						continue;
					}
					mission.setUpdateTime(new Date());
					//级联创建更新missionItem
					missionService.updateFull(mission,missionDB,storeId);
				} else {
					mission.setCreateTime(new Date());
					//级联创建更新missionItem
					missionService.updateFull(mission,null,storeId);
				}
				bindList.add(mission.getId());
			}
			missionList.setUpdateTime(now);
		}

		update(missionList, bindList,storeId);
	}
}

