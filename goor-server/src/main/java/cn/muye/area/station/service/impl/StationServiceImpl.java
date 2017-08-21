package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationMapPointXREF;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.service.StationMapPointXREFService;
import cn.muye.area.station.service.StationRobotXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/17
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
@Transactional
@Service
public class StationServiceImpl extends BaseServiceImpl<Station> implements StationService {

    @Autowired
    protected StationMapPointXREFService stationMapPointXREFService;
    @Autowired
    protected PointService pointService;
    @Autowired
    protected StationRobotXREFService stationRobotXREFService;
    @Autowired
    protected RobotService robotService;
    @Autowired
    private StationMapper stationMapper;

    @Override
    public int save(Station station) {
        int num = myMapper.insert(station);
        //关联点
        saveStationMapPointXREFByStation(station);
        return num;
    }

    @Override
    public int update(Station station) {
        //删除关联的点，然后重新添加关联的点
        stationMapPointXREFService.deleteByStationId(station.getId());

        int num = myMapper.updateByPrimaryKeySelective(station);

        //重新关联点
        saveStationMapPointXREFByStation(station);

        return num;
    }

    private void saveStationMapPointXREFByStation(Station station) {
        //重新关联点
        Long stationId = station.getId();
        List<MapPoint> mapPointList = station.getMapPoints();
        if (mapPointList != null && mapPointList.size() > 0) {
            for (MapPoint mapPoint : mapPointList) {
                StationMapPointXREF stationMapPointXREF = new StationMapPointXREF();
                stationMapPointXREF.setStationId(stationId);
                stationMapPointXREF.setMapPointId(mapPoint.getId());
                stationMapPointXREFService.save(stationMapPointXREF);
            }
        }
    }

    @Override
    public Station findById(long id, long storeId,Long sceneId) {
        Example example = new Example(Station.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ID =", id)
                .andCondition("ACTIVE =", Constant.NORMAL)
                .andCondition("STORE_ID =", storeId);
        if(sceneId != null) {
            criteria.andCondition("SCENE_ID =", sceneId);
        }
        example.setOrderByClause("ID DESC");
        List<Station> temp = myMapper.selectByExample(example);
        if (temp == null || temp.size() <= 0) {
            return null;
        }
        Station station = temp.get(0);
        List<MapPoint> resultMapPoint = new ArrayList<MapPoint>();
        List<StationMapPointXREF> stationMapPointXREFList = stationMapPointXREFService.listByStationId(id);
        //如果没有关联点，则直接返回
        if (stationMapPointXREFList != null && stationMapPointXREFList.size() > 0) {
            //如果有关联点，则更新station的点列表
            for (StationMapPointXREF stationMapPointXREF : stationMapPointXREFList) {
                MapPoint mapPoint = pointService.findById(stationMapPointXREF.getMapPointId());
                resultMapPoint.add(mapPoint);
            }
            station.setMapPoints(resultMapPoint);
        }

        return station;
    }

    @Override
    public List<Station> list(WhereRequest whereRequest, Long storeId,Long sceneId) {
        //如果whereRequest不为null，则分页
        if (whereRequest != null) {
            PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        }

        List<Station> stationList = new ArrayList<Station>();
        if (whereRequest != null && whereRequest.getQueryObj() != null && JSON.parseObject(whereRequest.getQueryObj()) != null) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());

            //TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
            /*result = myMapper.list(name);*/

            //方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
            Example example = new Example(Station.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andCondition("ACTIVE =", Constant.NORMAL)
                    .andCondition("STORE_ID =", storeId);
            Object name = map.get(SearchConstants.SEARCH_NAME);
            if(name != null) {
                criteria.andCondition("NAME like", "%" + name + "%");
            }
            if(sceneId != null) {
                criteria.andCondition("SCENE_ID =", sceneId);
            }
            example.setOrderByClause("ID DESC");
            stationList = myMapper.selectByExample(example);
        } else {
            //TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = myMapper.list(null);*/

            //方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
            Example example = new Example(Station.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andCondition("ACTIVE =", Constant.NORMAL);
            example.setOrderByClause("ID DESC");
            //超级管理员传storeId=null，能查看所有站；医院管理员传storeId!=null，只能查看该医院的站
            if (storeId != null) {
                criteria.andCondition("STORE_ID =", storeId);
            }
            if(sceneId != null) {
                criteria.andCondition("SCENE_ID =", sceneId);
            }
            stationList = myMapper.selectByExample(example);
        }

        //如果用公共Mapper查询，则需手动用For循环把关联的点放到站里面
        if (stationList != null && stationList.size() > 0) {
            for (Station station : stationList) {
                List<MapPoint> resultMapPoint = new ArrayList<MapPoint>();
                List<StationMapPointXREF> stationMapPointXREFList = stationMapPointXREFService.listByStationId(station.getId());
                //如果没有关联点，则直接返回
                if (stationMapPointXREFList == null || stationMapPointXREFList.size() <= 0) {
                    continue;
                }

                //如果有关联点，则更新station的点列表
                for (StationMapPointXREF stationMapPointXREF : stationMapPointXREFList) {
                    MapPoint mapPoint = pointService.findById(stationMapPointXREF.getMapPointId());
                    if (mapPoint == null) {
                        //如果关联的点不存在，手动删除点的关联关系
                        stationMapPointXREFService.deleteByPointId(stationMapPointXREF.getMapPointId());
                        continue;
                    }
                    resultMapPoint.add(mapPoint);
                }

                if (resultMapPoint != null && resultMapPoint.size() > 0) {
                    station.setMapPoints(resultMapPoint);
                }

            }
        }
        if (stationList != null && stationList.size() > 0) {
            for (Station station : stationList) {
                List<Robot> robotList = Lists.newArrayList();
                List<StationRobotXREF> stationRobotXrefDbList = stationRobotXREFService.getByStationId(station.getId());
                if (stationRobotXrefDbList != null && stationRobotXrefDbList.size() > 0) {
                    for (StationRobotXREF xref : stationRobotXrefDbList) {
                        Robot robotDb = robotService.getById(xref.getRobotId());
                        robotList.add(robotDb);
                    }
                }
                station.setRobotList(robotList);
            }
        }
        return stationList;
    }

    @Override
    public List<Station> listByName(String name,long storeId,long sceneId) {
        Example example = new Example(Station.class);
        example.createCriteria().andCondition("NAME =", name)
                .andCondition("SCENE_ID =", sceneId)
                .andCondition("STORE_ID =", storeId)
                .andCondition("ACTIVE =", Constant.NORMAL);
        return myMapper.selectByExample(example);
    }

    @Override
    public int delete(Station station) {
        //先删除关联的点
        stationMapPointXREFService.deleteByStationId(station.getId());
        //再删除站
        int num = myMapper.delete(station);
        return num;
    }

    @Override
    public void bindRobots(Station station) {
        if (station != null) {
            stationRobotXREFService.deleteByStationId(station.getId());
            for (Robot robot : station.getRobotList()) {
                StationRobotXREF stationRobotXREF = new StationRobotXREF();
                stationRobotXREF.setRobotId(robot.getId());
                stationRobotXREF.setStationId(station.getId());
                stationRobotXREFService.save(stationRobotXREF);
            }
        }
    }

    @Override
    public List<Station> findStationsByRobotCode(String robotCode) {
        return this.stationMapper.findStationsByRobotCode(robotCode);
    }
}

