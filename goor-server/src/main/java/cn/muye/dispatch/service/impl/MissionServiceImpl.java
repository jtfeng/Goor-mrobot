package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.mission.Mission;
import cn.mrobot.bean.mission.MissionMissionItemXREF;
import cn.mrobot.bean.mission.MissionItem;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionMapper;
import cn.muye.dispatch.mapper.MissionMissionItemXREFMapper;
import cn.muye.dispatch.mapper.MissionItemMapper;
import cn.muye.dispatch.service.MissionItemService;
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
public class MissionServiceImpl implements MissionService {

	@Autowired
	protected MissionMapper missionMapper;

	@Autowired
	protected MissionMissionItemXREFMapper missionMissionItemXREFMapper;

	@Autowired
	protected MissionItemMapper missionItemMapper;

	@Autowired
	protected MissionItemService missionItemService;

	@Override
	public long save(Mission mission) {
		return missionMapper.save(mission);
	}

	@Override
	public void update(Mission mission) {
		missionMapper.update(mission);
	}

	@Override
	public void updateFull(Mission mission, Mission missionDB,long storeId) {
		Date now = new Date();
		if(missionDB == null) {
			mission.setStoreId(storeId);
			mission.setCreateTime(now);
		}
		else {
			mission.setStoreId(missionDB.getStoreId());
		}
		//新建关联子任务，并更新关联关系
		Set<MissionItem> missionItemSet = mission.getMissionItemSet();
		List<Long> bindList = new ArrayList<Long>();
		if(missionItemSet != null && missionItemSet.size() > 0) {
			for(MissionItem missionItem : missionItemSet) {
				missionItem.setName(missionItem.getName() + now.getTime());
				if (missionItem.getId() != null) {
					MissionItem missionItemDB = missionItemService.get(missionItem.getId(),missionItem.getStoreId());
					//有id且数据库不存在的子任务不做处理
					if(missionItemDB == null) {
						continue;
					}
					missionItem.setUpdateTime(new Date());
					missionItemService.update(missionItem);
				} else {
					missionItem.setStoreId(storeId);
					missionItem.setCreateTime(new Date());
					missionItemService.save(missionItem);
				}
				bindList.add(missionItem.getId());
			}
			mission.setUpdateTime(now);
		}

		update(mission, bindList,storeId);
	}

	@Override
	public void update(Mission mission, List<Long> missionItemIdList,long storeId) {
		Long missionId = mission.getId();

		//添加关联关系,先全部删除，然后再关联
		if(missionId == null) {
			mission.setStoreId(storeId);
			missionMapper.save(mission);
			missionId = mission.getId();
		}
		else {
			missionMapper.update(mission);
			missionMissionItemXREFMapper.deleteByMissionId(missionId);
		}

		for (Long id : missionItemIdList) {
			//判断missionItem是否存在
			MissionItem missionItem = missionItemMapper.get(id,storeId);
			if(missionItem == null) {
				continue;
			}
			MissionMissionItemXREF missionMissionItemXREF = new MissionMissionItemXREF();
			missionMissionItemXREF.setMissionId(missionId);
			missionMissionItemXREF.setMissionItemId(id);
			missionMissionItemXREFMapper.save(missionMissionItemXREF);
		}
	}

//	@Override
//	public void update(Mission mission, List<MissionItem> missionItems) {
//		Long missionChainId = mission.getId();
//		MissionMissionItemXREF missionMissionItemXREF = new MissionMissionItemXREF();
//		missionMissionItemXREF.setMissionId(missionChainId);
//		//添加关联关系
//		List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByMissionId(missionChainId);
//		if(missionItems != null && missionItems.size() > 0) {
//			for (int i = 0; i < missionItems.size(); i++) {
//				missionMissionItemXREF.setMissionItemId(missionItems.get(i).getId());
//				if (! missionMissionItemXREFList.contains(missionMissionItemXREF)) {
//					missionMissionItemXREFMapper.save(missionMissionItemXREF);
//				}
//			}
//		}
//		missionMapper.update(mission);
//	}

	@Override
	public Mission get(long id,long storeId) {
		return missionMapper.get(id,storeId);
	}

	@Override
	public void delete(Mission mission,long storeId) {
		missionMissionItemXREFMapper.deleteByMissionId(mission.getId());//删除关联关系
		missionMapper.delete(mission.getId(),storeId);
	}

	@Override
	public Mission findByName(String name,long storeId) {
		return missionMapper.findByName(name,storeId);
	}

	@Override
	public List<Mission> list(WhereRequest whereRequest,long storeId) {
		List<Mission> missionList = new ArrayList<>();
		if (whereRequest != null && whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
//			Object missionMainId = map.get(SearchConstants.SEARCH_MISSION_MAIN_ID);
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object sceneName = map.get(SearchConstants.SEARCH_MISSION_SCENE_NAME);
			Object typeId = map.get(SearchConstants.SEARCH_MISSION_TYPE_ID);
			Object sceneId = map.get(SearchConstants.SEARCH_SCENE_ID);
//			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionList = missionMapper.list(/*missionMainId, */name, beginDate, endDate, sceneName, typeId/*, priority*/,storeId,sceneId);
		}else {
			missionList = missionMapper.listAll(storeId);
		}
		return bindMissionNodeList(missionList);
	}

	@Override
	public List<Mission> list(long storeId) {
		List<Mission> missionList = missionMapper.listAll(storeId);
		return bindMissionNodeList(missionList);
	}

	//查找出关联的子任务
	private List<Mission> bindMissionNodeList(List<Mission> missionList) {
		Mission mission;
		for (int i = 0; i < missionList.size(); i++) {
			Set<MissionItem> missionItemList = new HashSet<>();
			mission = missionList.get(i);
			List<MissionMissionItemXREF> missionMissionItemXREFList = missionMissionItemXREFMapper.findByMissionId(mission.getId());
			for (int j = 0; j < missionMissionItemXREFList.size(); j++) {
				missionItemList.add(missionMissionItemXREFList.get(j).getMissionItem());
			}
			mission.setMissionItemSet(missionItemList);
		}
		return missionList;
	}

}

