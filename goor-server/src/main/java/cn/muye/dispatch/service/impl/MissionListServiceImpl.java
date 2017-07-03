package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.Mission;
import cn.mrobot.bean.misssion.MissionList;
import cn.mrobot.bean.misssion.MissionListMissionXREF;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionListMissionXREFMapper;
import cn.muye.dispatch.mapper.MissionListMapper;
import cn.muye.dispatch.service.MissionListService;
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
public class MissionListServiceImpl implements MissionListService {

	@Autowired
    protected MissionListMapper missionListMapper;

	@Autowired
	protected MissionListMissionXREFMapper missionListMissionXREFMapper;

	@Override
	public long save(MissionList missionList) {
		return missionListMapper.save(missionList);
	}

	@Override
	public void update(MissionList missionList) {
		missionListMapper.update(missionList);
	}

	@Override
	public void update(MissionList missionList, List<Long> nodeIdList) {
		//添加关联关系
		MissionListMissionXREF missionListMissionXREF = new MissionListMissionXREF();
		missionListMissionXREF.setMissionMainId(missionList.getId());
		for (int i = 0; i < nodeIdList.size(); i++) {
			missionListMissionXREF.setMissionChainId(nodeIdList.get(i));
			missionListMissionXREFMapper.save(missionListMissionXREF);
		}
		missionListMapper.update(missionList);
	}

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
		missionListMissionXREFMapper.deleteByMainId(missionList.getId());
		missionListMapper.delete(missionList.getId());
	}

	@Override
    public List<MissionList> list(WhereRequest whereRequest) {
		List<MissionList> missionListList = new ArrayList<>();
		if(whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object deviceId = map.get(SearchConstants.SEARCH_DEVICE_ID);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionListList = missionListMapper.list(name, deviceId,beginDate,endDate,priority);
		}

        return bindMissionChain(missionListList);
    }

	@Override
	public List<MissionList> list() {
		return bindMissionChain(missionListMapper.listAll());
	}

	private List<MissionList> bindMissionChain(List<MissionList> missionListList){
		List<MissionList> result = new ArrayList<>();
		MissionList missionMain;
		for(int i = 0; i < missionListList.size(); i ++){
			List<Mission> missionList = new ArrayList<>();
			missionMain = missionListList.get(i);
			List<MissionListMissionXREF> missionListMissionXREFList = missionListMissionXREFMapper.findByMainId(missionMain.getId());
			for(int j = 0; j < missionListMissionXREFList.size(); j ++){
				missionList.add(missionListMissionXREFList.get(j).getMission());
			}
			missionMain.setMissionList(missionList);
			result.add(missionMain);
		}
		return result;
	}
}

