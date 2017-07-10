package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapZipMapper;
import cn.muye.area.map.service.MapZipService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 9:28
 * Describe:
 * Version:1.0
 */
@Service
public class MapZipServiceImpl implements MapZipService {

    @Autowired
    private MapZipMapper mapZipMapper;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @Override
    public void update(MapZip mapZip) {
        mapZipMapper.updateByPrimaryKey(mapZip);
    }

    @Override
    public MapZip getMapZip(long id) {
        MapZip mapZip = mapZipMapper.selectByPrimaryKey(id);
        if (mapZip != null) {
            mapZip.setFileHttpPath(parseLocalPath(mapZip.getFilePath()));
            return mapZip;
        }
        return null;
    }

    @Override
    public long save(MapZip mapZip) {
        mapZip.setCreateTime(new Date());
        return mapZipMapper.insert(mapZip);
    }

    @Override
    public void delete(MapZip mapZip) {
        //删除文件
        String filePath = mapZip.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        mapZipMapper.delete(mapZip);
    }

    @Override
    public List<MapZip> list(MapZip mapZip) {
        return parseLocalPath(mapZipMapper.select(mapZip));
    }

    @Override
    public List<MapZip> list(WhereRequest whereRequest, long storeId) {
        Example example = new Example(MapZip.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("STORE_ID =", storeId);
        if (whereRequest.getQueryObj() != null) {
            JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
            Object mapName = jsonObject.get(SearchConstants.SEARCH_MAP_NAME);
            Object sceneName = jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
            if (mapName != null) {
                criteria.andCondition("MAP_NAME like ", "%" + mapName + "%");
            }
            if (sceneName != null) {
                criteria.andCondition("SCENE_NAME like ", "%" + sceneName + "%");
            }
        }
        example.setOrderByClause("CREATE_TIME DESC");
        List<MapZip> mapZipList = mapZipMapper.selectByExample(example);
        return parseLocalPath(mapZipList);
    }

    @Override
    public MapZip latestZip(Long storeId) {
        Condition condition = new Condition(MapZip.class);
        Example.Criteria criteria = condition.createCriteria();
        if (storeId != null) {
            criteria.andCondition("storeId=" + storeId);
        }
        condition.setOrderByClause("CREATE_TIME desc");
        List<MapZip> mapZipList = mapZipMapper.selectByExample(condition);
        if (mapZipList.size() > 0) {
            MapZip mapZip = mapZipList.get(0);
            mapZip.setFileHttpPath(parseLocalPath(mapZip.getFilePath()));
            return mapZip;
        }
        return null;
    }

    private List<MapZip> parseLocalPath(List<MapZip> mapZipList) {
        List<MapZip> resultList = new ArrayList<>();
        for (int i = 0; i < mapZipList.size(); i++) {
            MapZip mapZip = mapZipList.get(i);
            if (mapZip != null) {
                mapZip.setFileHttpPath(parseLocalPath(mapZip.getFilePath()));
                resultList.add(mapZip);
            }
        }
        return resultList;
    }

    private String parseLocalPath(String localPath) {
        //将文件路径封装成http路径
        int index = localPath.indexOf(SearchConstants.FAKE_MERCHANT_STORE_ID + "");
        if (index >= 0) {
            return DOWNLOAD_HTTP + localPath.substring(index);
        }
        return "";
    }
}

