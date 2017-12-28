package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.roadpath.RoadPathPoint;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.roadpath.mapper.RoadPathLockMapper;
import cn.muye.assets.roadpath.mapper.RoadPathMapper;
import cn.muye.assets.roadpath.mapper.RoadPathPointMapper;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
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

            Long restrictedStarttimeLongTime = null, restrictedEndtimeLongTime = null;
            if (pathTypeInt.equals(Constant.PATH_TYPE_RESTRICTED)) {
                restrictedStarttimeLongTime = Long.valueOf(String.valueOf(body.get("restrictedStarttimeLongTime")));
                restrictedEndtimeLongTime = Long.valueOf(String.valueOf(body.get("restrictedEndtimeLongTime")));
            }

            //只有路径类型校验是云端类型,或者新增工控路径的才判断是不是有两个以上点
            if (pathTypeInt.equals(Constant.PATH_TYPE_CLOUD) || pathTypeInt.equals(Constant.PATH_TYPE_RESTRICTED)) {
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
                if (pathTypeInt.equals(Constant.PATH_TYPE_RESTRICTED)) {
                    roadPath.setRestrictedStarttimeLongTime(restrictedStarttimeLongTime);
                    roadPath.setRestrictedEndtimeLongTime(restrictedEndtimeLongTime);
                }
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

            //清空某场景、某门店下的路径相关的缓存
            PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
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

            //清空某场景、某门店下的路径相关的缓存
            PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
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
        example.setOrderByClause("ID DESC");
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
        example.setOrderByClause("ID DESC");
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return roadPaths;
    }

    @Override
    public List<RoadPath> listRoadPaths(WhereRequest whereRequest, Long storeId) throws Exception {

        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("STORE_ID =", storeId);
        // 判断分页查询的时候是否有传入查询关键字
        if (whereRequest.getQueryObj() != null && !"".equals(whereRequest.getQueryObj().trim())) {
            JSONObject queryObject = JSONObject.parseObject(whereRequest.getQueryObj());
            // 可能为空
            String pathNameKeyword = queryObject.getString("PATH_NAME".toLowerCase());
            if (pathNameKeyword != null && !"".equals(pathNameKeyword.trim())) {
                criteria.andCondition(" PATH_NAME LIKE ", "%"+pathNameKeyword.trim()+"%");
            }
            // 可能为空
            String sceneNameKeyword = queryObject.getString("SCENE_NAME".toLowerCase());
            if (sceneNameKeyword != null && !"".equals(sceneNameKeyword.trim())) {
                criteria.andCondition(" SCENE_NAME LIKE ", "%"+sceneNameKeyword.trim()+"%");
            }
            // 可能为空
            String mapNameKeyword = queryObject.getString("MAP_NAME".toLowerCase());
            if (mapNameKeyword != null && !"".equals(mapNameKeyword.trim())) {
                criteria.andCondition(" MAP_NAME = ", mapNameKeyword);
            }
            // 可能为空(此处暂定为 0 表示云端配置 1 代表工控上传）)
            String pathType = queryObject.getString("PATH_TYPE".toLowerCase());
            if (pathType != null && !"".equals(pathType.trim())) {
                if ("0".equals(pathType.trim()) || "1".equals(pathType.trim())) {
                    criteria.andCondition(" PATH_TYPE = ", Integer.parseInt(pathType.trim()));
                }else {
                    return Lists.newArrayList();
                }
            }
        }
        example.setOrderByClause("ID DESC");
        List<RoadPath> roadPaths = myMapper.selectByExample(example);
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

    @Override
    public RoadPath findBySceneAndX86RoadPathId(Long x86RoadPathId, String sceneName, String mapName, Long storeId) {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        if (sceneName != null) {
            criteria.andCondition("SCENE_NAME = ", sceneName);
        }
        if (mapName != null) {
            criteria.andCondition("MAP_NAME = ", mapName);
        }
        if (x86RoadPathId != null) {
            criteria.andCondition("PATH_ID = ", x86RoadPathId);
        }
        if(storeId != null) {
            criteria.andCondition("STORE_ID = ", storeId);
        }
        example.setOrderByClause("ID ASC");
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return roadPaths == null || roadPaths.size() == 0 ? null : roadPaths.get(0);
    }

    @Override
    public List<RoadPath> listRoadPathsBySceneNamePathType(String sceneName, Integer pathType, Long storeId) {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        if (sceneName != null) {
            criteria.andCondition("SCENE_NAME = ", sceneName);
        }
        if (pathType != null) {
            criteria.andCondition("PATH_TYPE = ", pathType);
        }
        if(storeId != null) {
            criteria.andCondition("STORE_ID = ", storeId);
        }
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return roadPaths;
    }

    @Override
    public List<RoadPath> listRoadPathsBySceneNamePathTypeOrderByStart(String sceneName, Integer pathType, Long storeId) {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        if (sceneName != null) {
            criteria.andCondition("SCENE_NAME = ", sceneName);
        }
        if (pathType != null) {
            criteria.andCondition("PATH_TYPE = ", pathType);
        }
        if(storeId != null) {
            criteria.andCondition("STORE_ID = ", storeId);
        }
        example.setOrderByClause("START_POINT ASC,ID ASC");
        List<RoadPath> roadPaths = this.roadPathMapper.selectByExample(example);
        return roadPaths;
    }

    @Override
    public List<RoadPathDetail> listRoadPathDetailsBySceneNamePathType(String sceneName, Integer pathType, Long storeId) {
        return packageRoadPathDetail(listRoadPathsBySceneNamePathType(sceneName, pathType, storeId));
    }

    private List<RoadPathDetail> packageRoadPathDetail(List<RoadPath> roadPaths) {
        List<RoadPathDetail> roadPathDetails = Lists.newArrayList();
        for (RoadPath roadPath : roadPaths) {
            RoadPathDetail roadPathDetail = new RoadPathDetail();
            //20171227 TODO 临时规避报错，记得去掉
            Date temp = new Date();
            roadPath.setRestrictedEndtime(temp);
            roadPath.setRestrictedStarttime(temp);
            roadPath.setRestrictedEndtimeLongTime(0L);
            roadPath.setRestrictedStarttimeLongTime(0L);
            //---------------------------
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

    @Override
    public void createRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception {
        this.roadPathMapper.insert(roadPath);
        packageRoadPathRelations(roadPathPointIds, roadPath);
        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
    }

    @Override
    public void updateRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception {
        Example example = new Example(RoadPath.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ID=" + roadPath.getId());
        this.roadPathMapper.updateByExampleSelective(roadPath,example);
        packageRoadPathRelations(roadPathPointIds, roadPath);
        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
    }

    @Override
    public void createOrUpdateRoadPathByStartAndEndPoint(Long startPointId, Long endPointId, String sceneName, String mapName, Integer pathType,RoadPath roadPath,List<Long> roadPathPointIds) throws Exception {
        List<RoadPath> roadPathList1 = listRoadPathByStartAndEndPoint(startPointId,
                endPointId,sceneName,mapName,pathType);
        if(roadPathList1 == null || roadPathList1.size() == 0 ) {
            createRoadPathByRoadPathPointList(roadPath,roadPathPointIds);
        }
        //有则更新第一个的路径详细序列和权值
        else {
            RoadPath roadPathDB = roadPathList1.get(0);
            roadPath.setId(roadPathDB.getId());
            updateRoadPathByRoadPathPointList(roadPath,roadPathPointIds);
        }
        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
    }

    @Autowired
    MapInfoService mapInfoService;
    @Autowired
    PointService pointService;
    /**
     * 根据PathDTOList插入点和工控路径
     * @param pathDTOList
     * @param sceneName
     * @param isPointDuplicate 是否建立重复的路径交点
     * @throws Exception
     */
    @Override
    public void saveOrUpdateRoadPathByPathDTOList(List<PathDTO> pathDTOList, String sceneName , boolean isPointDuplicate) throws Exception{
        //如果没有值，就不更新操作
        if(pathDTOList == null || pathDTOList.size() == 0 || StringUtil.isNullOrEmpty(sceneName)) {
            return;
        }

        Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

        //先删除该场景下的所有路径，云端路径和工控路径都删除
        //deleteBySceneName(sceneName);

        //再导入该场景的工控路径
        for (PathDTO pathDTO : pathDTOList) {
            //过滤掉地图名为null的错误数据
            if(null == pathDTO.getStartMap()
                    || "null".equals(pathDTO.getStartMap())
                    || null == pathDTO.getEndMap()
                    || "null".equals(pathDTO.getEndMap())) {
                log.info("!!!!!!pathDTO {} startMap OR endMap is null" + pathDTO.getId());
                continue;
            }

            //把开始点所属的MapInfo插入数据库(如果存在则不新建，如果不存在则新建)
            PathUtil.findOrSaveMapInfoByPath(sceneName, pathDTO, true, mapInfoService, storeId);
            //把开始点所属的MapInfo插入数据库(如果存在则不新建，如果不存在则新建)
            PathUtil.findOrSaveMapInfoByPath(sceneName, pathDTO, false, mapInfoService, storeId);

            MapPoint startPoint = null;
            MapPoint endPoint = null;
            if(isPointDuplicate) {
                startPoint = PathUtil.findOrSaveMapPointByPathDuplicate(sceneName, pathDTO, true,pointService, storeId);
                endPoint = PathUtil.findOrSaveMapPointByPathDuplicate(sceneName, pathDTO, false,pointService, storeId);
            }
            else {
                startPoint = PathUtil.findOrSaveMapPointByPathNoDuplicate(sceneName, pathDTO, true,pointService, storeId);
                endPoint = PathUtil.findOrSaveMapPointByPathNoDuplicate(sceneName, pathDTO, false,pointService, storeId);
            }

            //封装RoadPath对象，保存数据库
            RoadPath roadPath = new RoadPath();
            roadPath.setSceneName(sceneName);
            roadPath.setMapName(pathDTO.getStartMap());
            roadPath.setPathId(pathDTO.getId() + "");
            //添加roadpath查询，根据场景，地图，pathid进行查询，如果存在，则更新，不存在则添加
            RoadPath roadPathDB = findRoadPath(roadPath);
            //继续封装参数
            roadPath.setStartPoint(startPoint.getId());
            roadPath.setEndPoint(endPoint.getId());
            roadPath.setPathType(Constant.PATH_TYPE_X86);
            roadPath.setPathName(Constant.PATH + pathDTO.getId());
            roadPath.setCreateTime(new Date());
            roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
//            roadPath.setWeight(Constant.DEFAULT_ROAD_PATH_X86_WEIGHT);
            //取两点间长度作为路径权值
            roadPath.setWeight(PathUtil.calDistance(startPoint, endPoint));
            roadPath.setX86PathType(Constant.X86_PATH_TYPE_STRICT_DIRECTION);//默认有朝向要求
            //根据数据库查询结果判断是更新还是新增
            if (null != roadPathDB) {
                roadPath.setId(roadPathDB.getId());
                //更新
                updateSelectiveByStoreId(roadPath);
            } else {
                //新增
                save(roadPath);
            }
        }
        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
    }

    @Override
    public void saveOrUpdateRoadPathByPathDTOListDuplicatePoint(List<PathDTO> pathDTOList, String sceneName) throws Exception {
        saveOrUpdateRoadPathByPathDTOList(pathDTOList, sceneName, true);
    }

    @Override
    public void saveOrUpdateRoadPathByPathDTOListNoDuplicatePoint(List<PathDTO> pathDTOList, String sceneName) throws Exception {
        saveOrUpdateRoadPathByPathDTOList(pathDTOList, sceneName, false);
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
            //20171227 TODO 临时规避报错，记得去掉
            Date temp = new Date();
            roadPath.setRestrictedEndtime(temp);
            roadPath.setRestrictedStarttime(temp);
            roadPath.setRestrictedEndtimeLongTime(0L);
            roadPath.setRestrictedStarttimeLongTime(0L);
            //---------------------------
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
            //清空某场景、某门店下的路径相关的缓存
            PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
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
            roadPathPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
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

    /**
     * 删除某场景下的所有路径对象
     * @param sceneName
     */
    @Override
    public void deleteBySceneName(String sceneName, Long storeId) {
        try {
            List<RoadPath> roadPaths = listRoadPathsBySceneNamePathType(sceneName, null, storeId);
            if(roadPaths != null && roadPaths.size() > 0) {
                //如果是云端路径，则先删除roadPathPoint表
                for(RoadPath roadPath : roadPaths) {
                    if(roadPath.getPathType().equals(Constant.PATH_TYPE_CLOUD)) {
                        this.roadPathMapper.deleteRoadPathPointsByPathId(roadPath.getId());
                    }
                }

                //再删除该场景下所有roadPath
                Example example = new Example(RoadPath.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andCondition("SCENE_NAME = ", sceneName);
                this.roadPathMapper.deleteByExample(example);
                //清空某场景、某门店下的路径相关的缓存
                PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteBySceneMapNameType(String sceneName, Integer pathType, String mapName, Long storeId) {
        try {
            List<RoadPath> roadPaths = listRoadPathsBySceneNamePathType(sceneName, pathType, storeId);
            if(roadPaths != null && roadPaths.size() > 0) {
                //如果是云端路径，则先删除roadPathPoint表
                if(pathType.equals(Constant.PATH_TYPE_CLOUD)) {
                    for(RoadPath roadPath : roadPaths) {
                        this.roadPathMapper.deleteRoadPathPointsByPathId(roadPath.getId());
                    }
                }

                //再删除该场景下所有roadPath
                Example example = new Example(RoadPath.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andCondition("SCENE_NAME = ", sceneName);
                if(pathType != null) {
                    criteria.andCondition("PATH_TYPE = ", pathType);
                }
                if(mapName != null) {
                    criteria.andCondition("MAP_NAME = ", mapName);
                }
                this.roadPathMapper.deleteByExample(example);
                //清空某场景、某门店下的路径相关的缓存
                PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByStartEndPointIdType(Long startPointId, Long endPointId, Integer pathType, String sceneName, Long storeId) {
        try {
            List<RoadPath> roadPaths = listRoadPathsBySceneNamePathType(sceneName, null, storeId);
            if(roadPaths != null && roadPaths.size() > 0) {
                //如果是云端路径，则先删除roadPathPoint表
                for(RoadPath roadPath : roadPaths) {
                    if(roadPath.getPathType().equals(Constant.PATH_TYPE_CLOUD)) {
                        this.roadPathMapper.deleteRoadPathPointsByPathId(roadPath.getId());
                    }
                }

                //再删除该场景下所有roadPath
                Example example = new Example(RoadPath.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andCondition("SCENE_NAME = ", sceneName);
                if(pathType != null) {
                    criteria.andCondition("PATH_TYPE = ", pathType);
                }
                if(startPointId != null) {
                    criteria.andCondition("START_POINT = ", startPointId);
                }
                if(endPointId != null) {
                    criteria.andCondition("END_POINT = ", endPointId);
                }
                this.roadPathMapper.deleteByExample(example);
                //清空某场景、某门店下的路径相关的缓存
                PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}