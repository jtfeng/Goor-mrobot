package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.MissionChain;
import cn.mrobot.bean.misssion.MissionMain;
import cn.mrobot.bean.misssion.MissionMainChainXREF;
import cn.mrobot.utils.WhereRequest;
import cn.muye.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionMainChainXREFMapper;
import cn.muye.dispatch.mapper.MissionMainMapper;
import cn.muye.dispatch.service.MissionMainService;
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
public class MissionMainServiceImpl implements MissionMainService {

	@Autowired
    protected MissionMainMapper missionMainMapper;

	@Autowired
	protected MissionMainChainXREFMapper missionMainChainXREFMapper;

	@Override
	public long save(MissionMain missionMain) {
		return missionMainMapper.save(missionMain);
	}

	@Override
	public void update(MissionMain missionMain) {
		missionMainMapper.update(missionMain);
	}

	@Override
	public void update(MissionMain missionMain, List<Long> nodeIdList) {
		//添加关联关系
		MissionMainChainXREF missionMainChainXREF = new MissionMainChainXREF();
		missionMainChainXREF.setMissionMainId(missionMain.getId());
		for (int i = 0; i < nodeIdList.size(); i++) {
			missionMainChainXREF.setMissionChainId(nodeIdList.get(i));
			missionMainChainXREFMapper.save(missionMainChainXREF);
		}
		missionMainMapper.update(missionMain);
	}

	@Override
    public MissionMain get(long id) {
        return missionMainMapper.get(id);
    }

	@Override
	public MissionMain findByName(String name) {
		return missionMainMapper.findByName(name);
	}

	@Override
	public void delete(MissionMain missionMain) {
		//删除关联关系
		missionMainChainXREFMapper.deleteByMainId(missionMain.getId());
		missionMainMapper.delete(missionMain.getId());
	}

	@Override
    public List<MissionMain> list(WhereRequest whereRequest) {
		List<MissionMain> missionMainList = new ArrayList<>();
		if(whereRequest.getQueryObj() != null){
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object deviceId = map.get(SearchConstants.SEARCH_DEVICE_ID);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionMainList = missionMainMapper.list(name, deviceId,beginDate,endDate,priority);
		}

        return bindMissionChain(missionMainList);
    }

	@Override
	public List<MissionMain> list() {
		return bindMissionChain(missionMainMapper.listAll());
	}

	private List<MissionMain> bindMissionChain(List<MissionMain> missionMainList){
		List<MissionMain> result = new ArrayList<>();
		MissionMain missionMain;
		for(int i =0; i < missionMainList.size(); i ++){
			List<MissionChain> missionChainList = new ArrayList<>();
			missionMain = missionMainList.get(i);
			List<MissionMainChainXREF> missionMainChainXREFList = missionMainChainXREFMapper.findByMainId(missionMain.getId());
			for(int j = 0; j < missionMainChainXREFList.size(); j ++){
				missionChainList.add(missionMainChainXREFList.get(j).getMissionChain());
			}
			missionMain.setMissionChainList(missionChainList);
			result.add(missionMain);
		}
		return result;
	}
}

