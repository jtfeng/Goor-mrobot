package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapInfoMapper;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/7/5
 * Time: 15:05
 * Describe:
 * Version:1.0
 */
@Service
public class MapInfoServiceImpl implements MapInfoService {

    @Autowired
    private MapInfoMapper mapInfoMapper;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @Override
    public long save(MapInfo mapInfo) {
        return mapInfoMapper.insert(mapInfo);
    }

    @Override
    public MapInfo getMapInfo(long id) {
        MapInfo mapInfo = mapInfoMapper.selectByPrimaryKey(id);
        mapInfo.setPngImageHttpPath(parseLocalPath(mapInfo.getPngImageLocalPath()));
        return mapInfo;
    }

    @Override
    public List<MapInfo> getMapInfo(String name, String sceneName, long storeId) {
        Condition example = new Condition(MapInfo.class);
        example.createCriteria().andCondition("MAP_NAME = '" + name + "'")
                .andCondition("SCENE_NAME = '" + sceneName + "'")
                .andCondition("STORE_ID = " + storeId + "");
        return mapInfoMapper.selectByExample(example);
    }

    @Override
    public void delete(MapInfo mapInfo) {
        mapInfoMapper.delete(mapInfo);
    }

    @Override
    public void update(MapInfo mapInfo) {
        mapInfoMapper.updateByPrimaryKey(mapInfo);
    }

    @Override
    public List<MapInfo> getMapInfo(WhereRequest whereRequest, long storeId) {
        Condition example = new Condition(MapInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (whereRequest.getQueryObj() != null) {
            JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
            Object mapName = jsonObject.get(SearchConstants.SEARCH_MAP_NAME);
            Object sceneName = jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
            if (sceneName != null) {
                criteria.andCondition("SCENE_NAME='" + sceneName + "'");
            }
            if (mapName != null) {
                criteria.andCondition("MAP_NAME='" + mapName + "'");
            }
        }
        criteria.andCondition("STORE_ID = " + storeId + "");
        return parseLocalPath(mapInfoMapper.selectByExample(example));
    }

    private List<MapInfo> parseLocalPath(List<MapInfo> mapInfoList) {
        List<MapInfo> resultList = new ArrayList<>();
        for (int i = 0; i < mapInfoList.size(); i++) {
            MapInfo mapInfo = mapInfoList.get(i);
            mapInfo.setPngImageHttpPath(parseLocalPath(mapInfo.getPngImageLocalPath()));
            mapInfo.setPngDesigned(parseLocalPath(mapInfo.getPngDesigned()));
            resultList.add(mapInfo);
        }
        return resultList;
    }

    private String parseLocalPath(String localPath) {
        if(StringUtil.isNullOrEmpty(localPath))
            return "";
        //将文件路径封装成http路径
        localPath = localPath.replaceAll("\\\\", "/");
        return DOWNLOAD_HTTP + localPath;
    }

}
