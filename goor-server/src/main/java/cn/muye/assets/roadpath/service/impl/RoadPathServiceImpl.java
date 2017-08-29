package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.roadpath.RoadPathPoint;
import cn.mrobot.bean.assets.scene.Scene;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.roadpath.mapper.RoadPathMapper;
import cn.muye.assets.roadpath.mapper.RoadPathPointMapper;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.base.service.imp.BaseServiceImpl;
import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
@Transactional
public class RoadPathServiceImpl extends BaseServiceImpl<RoadPath> implements RoadPathService {

    private static final Logger log = LoggerFactory.getLogger(RoadPathServiceImpl.class);
    @Autowired
    private RoadPathMapper roadPathMapper;
    @Autowired
    private RoadPathPointMapper roadPathPointMapper;
    @Autowired
    private MapPointMapper mapPointMapper;
    @Autowired
    private SceneMapper sceneMapper;

    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public void createRoadPath(Map<String, Object> body) throws Exception {
        // 拟合方式现假定有三种 （1：直线；2：曲线；3.云端定义）
        // String -> pathName
        // String -> pattern
        // String -> data
        // List<Long> -> points -> 前端传递格式为 一个数组
        // Long cloudSceneId
        String pathName = String.valueOf(checkNotNull(body.get("pathName"), "路径名称不允许为空，请重新输入!"));
        log.info(String.format("路径名称：%s", pathName));
        String pattern = String.valueOf(checkNotNull(body.get("pattern"), "路径拟合方式信息不允许为空，请重新输入!"));
        log.info(String.format("路径拟合方式：%s", pattern));
        String data = String.valueOf(checkNotNull(body.get("data"), "路径相关数据不允许为空，请重新输入!"));
        log.info(String.format("路径相关数据：%s", data));
        Long cloudSceneId = Long.parseLong(String.valueOf(checkNotNull(body.get("cloudSceneId"),"场景 ID 信息不能为空，请重新输入!")));
        log.info(String.format("场景编号信息为：%s", cloudSceneId));
        Long weight = Long.parseLong(String.valueOf(checkNotNull(body.get("weight"), "路径权值数据不允许为空，请重新输入!")));
        log.info(String.format("路径权值数据为：%s", weight));
        List points = (List)checkNotNull(body.get("points"),"点组合不允许为空，请重新选择!");
        checkArgument(points.size() >= 2, "点组合至少需要两个点（开始点和结束点）");
        // 保存路径上具体点的信息以及联系关系
        Scene receiveScene = this.sceneMapper.selectByPrimaryKey(cloudSceneId);
        String receiveMapSceneName = this.roadPathMapper.findMapSceneName(receiveScene.getId());// 对应的地图场景名城
        Set<String> checkContainer = Sets.newHashSet();
        for (Object pointId : points) {
            MapPoint mapPoint = this.mapPointMapper.selectByPrimaryKey(Long.parseLong(String.valueOf(pointId)));// 依次遍历每一个接收到的点信息并且进行处理
            if (!mapPoint.getSceneName().trim().equals(receiveMapSceneName.trim())){
                throw new Exception("传入的点与传入的场景信息不符，请重新选择!");
            }
            checkContainer.add(new StringBuilder().append(mapPoint.getSceneName())
                .append("|").append(mapPoint.getMapName()).append("|").append(mapPoint.getStoreId()).toString());
        }
        checkArgument(checkContainer.size() == 1, "传入的点组合不在同一张地图上，请重新选择");
        // 首先保存路径信息
        RoadPath roadPath = new RoadPath(){{
            setCloudSceneId(cloudSceneId);setData(data);setPattern(pattern);setPathName(pathName);
            setCreateTime(new Date());setStoreId(100L); setWeight(weight);
            setStartPoint(Long.parseLong(String.valueOf(points.get(0))));               // 设置开始点
            setEndPoint(Long.parseLong(String.valueOf(points.get(points.size() - 1)))); // 设置结束点
        }};
        this.roadPathMapper.insert(roadPath);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        //        // 当前点 id 信息
        //        private Long pointId;
        //        // 下一个点 id 信息
        //                  private Long nextPointId;
        //        // 是否为开始点的标记（1：是、0：否）
        //                  private Integer startFlag;
        //        // 是否为结束点的标记（1：是、0：否）
        //                  private Integer endFlag;
        //        // 对应绑定的路径 id 编号
        //        private Long roadPathId;
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        List<RoadPathPoint> roadPathPoints = Lists.newArrayList();
        int i = 0;
        for (Object pointIdOrign : points) {
            Long pointId = Long.parseLong(String.valueOf(pointIdOrign));
            RoadPathPoint roadPathPoint = new RoadPathPoint();
            roadPathPoint.setCreateTime(new Date());roadPathPoint.setStoreId(100L);
            roadPathPoint.setRoadPathId(roadPath.getId());// 设置路径信息 id 编号
            roadPathPoint.setPointId(pointId);// 当前点 id
            roadPathPoint.setStartFlag(0);roadPathPoint.setEndFlag(0);
            if (i == 0){
                roadPathPoint.setStartFlag(1);// 标记为开始点
            }
            if (i == (points.size() - 1)){
                RoadPathPoint prevPoint = roadPathPoints.get(i - 1);
                roadPathPoint.setPrevPointId(prevPoint.getPointId());
                roadPathPoint.setEndFlag(1);// 标记为结束点
            }
            if (i > 0 && i < (points.size())) {
                // 设置下一个点的信息
                RoadPathPoint prevPoint = roadPathPoints.get(i - 1);// 取得上一个点信息
                prevPoint.setNextPointId(pointId);// 设置下一个点信息
                if (i > 1) {
                    RoadPathPoint prevPrevPoint = roadPathPoints.get(i - 2);// 取得上上一个点信息
                    prevPoint.setPrevPointId(prevPrevPoint.getPointId());// 设置上上一个节点信息
                }
                roadPathPoints.set(i - 1, prevPoint);
            }
            roadPathPoints.add(roadPathPoint);
            i ++ ;
        }
        // 关系生成完毕之后 ， 保存一系列数据到数据库中
        this.roadPathPointMapper.insertList(roadPathPoints);// 批量保存数据信息
    }

    @Override
    public List<RoadPathDetail> findRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, Long cloudSceneId) throws Exception {
        Example example = new Example(RoadPath.class);
        example.createCriteria().andCondition("START_POINT = ", startPoint)
            .andCondition("END_POINT = ", endPoint)
            .andCondition("CLOUD_SCENE_ID = ", cloudSceneId);
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        List<RoadPathDetail> roadPathDetails = Lists.newArrayList();
        for (RoadPath roadPath : roadPaths) {
            RoadPathDetail roadPathDetail = new RoadPathDetail();
            BeanUtils.copyProperties(roadPath, roadPathDetail); // 拷贝到一个新的对象中
            roadPathDetail.setStart(this.mapPointMapper.selectByPrimaryKey(roadPath.getStartPoint()));
            roadPathDetail.setEnd(  this.mapPointMapper.selectByPrimaryKey(roadPath.getEndPoint()));

            List<MapPoint> relatePoints = Lists.newArrayList();
            RoadPathPoint begin = this.roadPathMapper.findBeginRoadPathPoint(roadPath.getId()).get(0);// 起始点
            relatePoints.add(this.mapPointMapper.selectByPrimaryKey(begin.getPointId()));// 加入第一个点
            RoadPathPoint nextRoadPathPointInfo = begin; // 表示下一个 引用点
            while ((nextRoadPathPointInfo = (nextRoadPathPointInfo.getNextPointId() == null ? null :
                this.roadPathMapper.findSpecifyRoadPathPoint(roadPath.getId(), nextRoadPathPointInfo.getNextPointId()).get(0))) != null) {
                relatePoints.add(this.mapPointMapper.selectByPrimaryKey(nextRoadPathPointInfo.getPointId()));// 加入第一个点
            }
            roadPathDetail.setRelatePoints(relatePoints);
            roadPathDetails.add(roadPathDetail);
        }
        return roadPathDetails;
    }
}