package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.roadpath.RoadPathPoint;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.roadpath.mapper.RoadPathLockMapper;
import cn.muye.assets.roadpath.mapper.RoadPathMapper;
import cn.muye.assets.roadpath.mapper.RoadPathPointMapper;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
    @Autowired
    private RoadPathLockMapper roadPathLockMapper;
    private String sceneName = null;
    private String mapName = null;

    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public void createRoadPath(Map<String, Object> body) throws Exception {
        // 拟合方式现假定有三种 （1：直线；2：曲线；3.云端定义）
        // String -> pathName
        // String -> pattern
        // String -> data
        // List<Long> -> points -> 前端传递格式为 一个数组

        // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
        try {
            String pathName = String.valueOf(checkNotNull(body.get("pathName"), "路径名称不允许为空，请重新输入!"));
            log.info(String.format("路径名称：%s", pathName));

            String pattern = String.valueOf(checkNotNull(body.get("pattern"), "路径拟合方式信息不允许为空，请重新输入!"));
            log.info(String.format("路径拟合方式：%s", pattern));

            String data = String.valueOf(checkNotNull(body.get("data"), "路径相关数据不允许为空，请重新输入!"));
            log.info(String.format("路径相关数据：%s", data));

            Long weight = Long.parseLong(String.valueOf(checkNotNull(body.get("weight"), "路径权值数据不允许为空，请重新输入!")));
            log.info(String.format("路径权值数据为：%s", weight));

            String pathType = String.valueOf(checkNotNull(body.get("pathType"), "路径类型不能为空"));
            Integer pathTypeInt = Integer.parseInt(pathType);
            checkNotNull(pathTypeInt, "路径类型不能为空");
            List points = (List) checkNotNull(body.get("points"), "点组合不允许为空，请重新选择!");

            //只有路径类型校验是云端类型,或者新增工控路径的才判断是不是有两个以上点
            if (pathTypeInt.equals(Constant.PATH_TYPE_CLOUD)) {
                checkArgument(points.size() >= 2, "点组合至少需要两个点（开始点和结束点）");
                Long startPointId = Long.parseLong(String.valueOf(points.get(0)));
                RoadPath roadPath = new RoadPath() {{
                    setData(data);
                    setPattern(pattern);
                    setPathName(pathName);
                    setCreateTime(new Date());
                    setStoreId(100L);
                    setWeight(weight);
                    setStartPoint(startPointId);               // 设置开始点
                    setEndPoint(Long.parseLong(String.valueOf(points.get(points.size() - 1)))); // 设置结束点
                    setPathId("");//云端路径没有PathId
                    setPathType(pathTypeInt);
                }};
                setRoadPathSceneMapNameByPoint(roadPath, startPointId);
                this.roadPathMapper.insert(roadPath);
                packageRoadPathRelations(points, roadPath);
            } else if (pathTypeInt.equals(Constant.PATH_TYPE_X86)) {
                checkArgument(points.size() == 2, "工控路径点组合只能两个点（开始点和结束点）");
                Long startPointId = Long.parseLong(String.valueOf(points.get(0)));
                //如果是工控路径还得校验工控路径不为空
                String pathId = String.valueOf(checkNotNull(body.get("pathId"), "工控路径ID不能为空"));
                RoadPath roadPath = new RoadPath() {{
                    setData(data);
                    setPattern(pattern);
                    setPathName(pathName);
                    setCreateTime(new Date());
                    setStoreId(100L);
                    setWeight(weight);
                    setStartPoint(Long.parseLong(String.valueOf(points.get(0))));               // 设置开始点
                    setEndPoint(Long.parseLong(String.valueOf(points.get(1)))); // 设置结束点
                    setPathType(pathTypeInt);
                    setPathId(pathId);
                }};
                setRoadPathSceneMapNameByPoint(roadPath, startPointId);
                this.roadPathMapper.insert(roadPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateRoadPath(Map<String, Object> body) throws Exception {
        try {
            Long id = Long.parseLong(String.valueOf(checkNotNull(body.get("id"), "id编号不允许为空，请重新输入!")));
            log.info(String.format("id编号信息为：%s", id));
            String pathName = String.valueOf(checkNotNull(body.get("pathName"), "路径名称不允许为空，请重新输入!"));
            log.info(String.format("4路径名称：%s", pathName));
            String pattern = String.valueOf(checkNotNull(body.get("pattern"), "路径拟合方式信息不允许为空，请重新输入!"));
            log.info(String.format("路径拟合方式：%s", pattern));
            String data = String.valueOf(checkNotNull(body.get("data"), "路径相关数据不允许为空，请重新输入!"));
            log.info(String.format("路径相关数据：%s", data));
            Long weight = Long.parseLong(String.valueOf(checkNotNull(body.get("weight"), "路径权值数据不允许为空，请重新输入!")));
            log.info(String.format("路径权值数据为：%s", weight));
            String pathType = String.valueOf(checkNotNull(body.get("pathType"), "路径类型不能为空"));
            Integer pathTypeInt = Integer.parseInt(pathType);
            checkNotNull(pathTypeInt, "路径类型不能为空");

            RoadPath roadPath = new RoadPath();
            roadPath.setId(id);
            roadPath.setPathType(pathTypeInt);
            roadPath.setData(data); // 路径数据
            roadPath.setPattern(pattern); // 拟合方式
            roadPath.setPathName(pathName); // 路径名称
            roadPath.setWeight(weight); // 路径权重大小
            roadPath.setSceneName(sceneName); // 工控场景名
            roadPath.setMapName(mapName);     // 工控地图名 【此处不知道是否需要更新工控路径 ID 信息以及路径类型】
            roadPath.setCreateTime(new Date());
            roadPath.setStoreId(100L);

            //只有路径类型校验是云端类型,或者新增工控路径的才判断是不是有两个以上点
            if (pathTypeInt.equals(Constant.PATH_TYPE_CLOUD)) {
                List points = (List) checkNotNull(body.get("points"), "云端路径点组合不允许为空，请重新选择!");
                checkArgument(points.size() >= 2, "云端路径点组合至少需要两个点（开始点和结束点）");
                roadPath.setStartPoint(Long.parseLong(String.valueOf(points.get(0))));               // 设置（开始点）
                roadPath.setEndPoint(Long.parseLong(String.valueOf(points.get(points.size() - 1)))); // 设置（结束点）

                Long startPointId = Long.parseLong(String.valueOf(points.get(0)));
                setRoadPathSceneMapNameByPoint(roadPath, startPointId);

                int updateCount = this.roadPathMapper.updateByPrimaryKeySelective(roadPath);
                log.info("当前更新的数据记录条数为 ：" + updateCount);
                packageRoadPathRelations(points, roadPath);
                return;
            } else if (pathTypeInt.equals(Constant.PATH_TYPE_X86)) {
                //如果是工控路径还得判断路径ID不为空
                String pathId = String.valueOf(checkNotNull(body.get("pathId"), "路径 ID 编号不允许为空，请重新输入!"));
                log.info(String.format("路径编号信息为：%s", pathId));
                roadPath.setPathId(pathId);

                List points = (List) body.get("points");
                if (points != null && points.size() > 0) {
                    checkArgument(points.size() == 2, "工控路径点组合至少只能有两个点（开始点和结束点）");
                    roadPath.setStartPoint(Long.parseLong(String.valueOf(points.get(0))));               // 设置（开始点）
                    roadPath.setEndPoint(Long.parseLong(String.valueOf(points.get(1)))); // 设置（结束点）

                    Long startPointId = Long.parseLong(String.valueOf(points.get(0)));
                    setRoadPathSceneMapNameByPoint(roadPath, startPointId);
                }

                int updateCount = this.roadPathMapper.updateByPrimaryKeySelective(roadPath);
                log.info("当前更新的数据记录条数为 ：" + updateCount);
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据出发点设置roadPath的场景名和地图名
     *
     * @param roadPath
     * @param startPointId
     */
    private void setRoadPathSceneMapNameByPoint(RoadPath roadPath, Long startPointId) {
        MapPoint mapPoint = mapPointMapper.selectByPrimaryKey(startPointId);
        roadPath.setSceneName(mapPoint.getSceneName());
        roadPath.setMapName(mapPoint.getMapName());
    }

    @Override
    public List<RoadPathDetail> findRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName) throws Exception {
        Example example = new Example(RoadPath.class);
        if (sceneName == null || mapName == null) {
            example.createCriteria().andCondition("START_POINT = ", startPoint)
                    .andCondition("END_POINT = ", endPoint);
        } else {
            example.createCriteria().andCondition("START_POINT = ", startPoint)
                    .andCondition("END_POINT = ", endPoint)
                    .andCondition("SCENE_NAME = ", sceneName)
                    .andCondition("MAP_NAME = ", mapName);
        }
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return packageRoadPathDetail(roadPaths);
    }

    @Override
    public List<RoadPathDetail> listRoadPathDetailByStartAndEndPointType(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("START_POINT = ", startPoint)
                .andCondition("END_POINT = ", endPoint);
        if (sceneName != null) {
            criteria.andCondition("SCENE_NAME = ", sceneName);
        }
        if (mapName != null) {
            criteria.andCondition("MAP_NAME = ", mapName);
        }
        if (pathType != null) {
            criteria.andCondition("PATH_TYPE = ", pathType);
        }
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return packageRoadPathDetail(roadPaths);
    }

    @Override
    public List<RoadPath> listRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("START_POINT = ", startPoint)
                .andCondition("END_POINT = ", endPoint);
        if (sceneName != null) {
            criteria.andCondition("SCENE_NAME = ", sceneName);
        }
        if (mapName != null) {
            criteria.andCondition("MAP_NAME = ", mapName);
        }
        if (pathType != null) {
            criteria.andCondition("PATH_TYPE = ", pathType);
        }
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return roadPaths;
    }

    @Override
    public List<RoadPath> listRoadPaths(WhereRequest whereRequest) throws Exception {
        List<RoadPath> roadPaths = listPageByStoreIdAndOrder(
                whereRequest.getPage(),
                whereRequest.getPageSize(),
                RoadPath.class,
                "ID DESC");
        detailRoadPath(roadPaths);
        return roadPaths;
    }

    @Override
    public Boolean hasRelatedRoadPath(Long id) {
        RoadPath roadPath = new RoadPath();
        roadPath.setPathLock(id);
        int count = myMapper.selectCount(roadPath);
        return count > 0 ? true : false;
    }

    @Override
    public RoadPath findRoadPath(RoadPath roadPath) throws Exception {
        return roadPathMapper.selectOne(roadPath);
    }

    private List<RoadPathDetail> packageRoadPathDetail(List<RoadPath> roadPaths) {
        List<RoadPathDetail> roadPathDetails = Lists.newArrayList();
        for (RoadPath roadPath : roadPaths) {
            RoadPathDetail roadPathDetail = new RoadPathDetail();
            BeanUtils.copyProperties(roadPath, roadPathDetail); // 拷贝到一个新的对象中
            roadPathDetail.setStart(this.mapPointMapper.selectByPrimaryKey(roadPath.getStartPoint()));
            roadPathDetail.setEnd(this.mapPointMapper.selectByPrimaryKey(roadPath.getEndPoint()));
            if (roadPath.getPathLock() != null) {
                // 当逻辑锁对象不为空时，才级联查询对应的管理锁对象
                roadPathDetail.setRoadPathLock(this.roadPathLockMapper.selectByPrimaryKey(roadPath.getPathLock()));//设置对应的逻辑锁对象
            }
            List<MapPoint> relatePoints = Lists.newArrayList();
            log.info("packageRoadPathDetail: start- " + roadPath.getStartPoint()
                    + ",end- " + roadPath.getEndPoint()
                    + ",roadPathId- " + roadPath.getId());
            try {
                for (RoadPathPoint roadPathPoint : this.roadPathMapper.findRoadPathPointByRoadPath(roadPath.getId())) {
                    relatePoints.add(this.mapPointMapper.selectByPrimaryKey(roadPathPoint.getPointId()));
                }
                roadPathDetail.setRelatePoints(relatePoints);
                roadPathDetails.add(roadPathDetail);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
        }
        return roadPathDetails;
    }

    /**
     * 不能返回一个新的 List，因为这样会破坏分页插件显示数据的正确性
     *
     * @param roadPaths
     */
    private void detailRoadPath(List<RoadPath> roadPaths) {
        int i = 0;
        for (RoadPath roadPath : roadPaths) {
            RoadPathDetail roadPathDetail = new RoadPathDetail();
            BeanUtils.copyProperties(roadPath, roadPathDetail); // 拷贝到一个新的对象中
            roadPathDetail.setStart(this.mapPointMapper.selectByPrimaryKey(roadPath.getStartPoint()));
            roadPathDetail.setEnd(this.mapPointMapper.selectByPrimaryKey(roadPath.getEndPoint()));
            if (roadPath.getPathLock() != null) {
                // 当逻辑锁对象不为空时，才级联查询对应的管理锁对象
                roadPathDetail.setRoadPathLock(this.roadPathLockMapper.selectByPrimaryKey(roadPath.getPathLock()));//设置对应的逻辑锁对象
            }
            List<MapPoint> relatePoints = Lists.newArrayList();
            log.info("packageRoadPathDetail: start- " + roadPath.getStartPoint()
                    + ",end- " + roadPath.getEndPoint()
                    + ",roadPathId- " + roadPath.getId());
            for (RoadPathPoint roadPathPoint : this.roadPathMapper.findRoadPathPointByRoadPath(roadPath.getId())) {
                relatePoints.add(this.mapPointMapper.selectByPrimaryKey(roadPathPoint.getPointId()));
            }
            roadPathDetail.setRelatePoints(relatePoints);
            roadPaths.set(i, roadPathDetail);
            i++;
        }
    }

    @Override
    public int deleteById(Long id) {
        try {
            int deleteRowCount = super.deleteById(id);
            this.roadPathMapper.deleteRoadPathPointsByPathId(id);//删除与路径关联的 MapPoint 信息
            return deleteRowCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void packageRoadPathRelations(List points, RoadPath roadPath) {
        // 首先删除旧的关系 ， 再添加新的数据关系
        this.roadPathMapper.deleteRoadPathPointsByPathId(roadPath.getId());
        List<RoadPathPoint> roadPathPoints = Lists.newArrayList();
        int i = 0;
        for (int j = 0; j < points.size(); j++) {
            Object pointIdOrign = points.get(j);
            Long pointId = Long.parseLong(String.valueOf(pointIdOrign));
            RoadPathPoint roadPathPoint = new RoadPathPoint();
            roadPathPoint.setCreateTime(new Date());
            roadPathPoint.setStoreId(100L);
            roadPathPoint.setRoadPathId(roadPath.getId());// 设置路径信息 id 编号
            roadPathPoint.setPointId(pointId);// 当前点 id
            roadPathPoint.setOrderIndex(j);//设置排序索引
            roadPathPoint.setStartFlag(0);
            roadPathPoint.setEndFlag(0);
            if (i == 0) {
                roadPathPoint.setStartFlag(1);// 标记为开始点
            }
            if (i == (points.size() - 1)) {
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
            i++;
        }
        // 关系生成完毕之后 ， 保存一系列数据到数据库中
        this.roadPathPointMapper.insertList(roadPathPoints);// 批量保存数据信息
    }
}