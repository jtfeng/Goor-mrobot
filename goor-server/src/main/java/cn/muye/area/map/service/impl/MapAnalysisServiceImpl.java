package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.SceneMapZipXREF;
import cn.mrobot.bean.area.point.IndustrialControlPointType;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.ZipUtils;
import cn.muye.area.map.service.MapAnalysisService;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.map.service.MapZipService;
import cn.muye.area.map.service.SceneMapZipXREFService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * @author Jelynn
 * @date 2017/7/26
 */
@Service
public class MapAnalysisServiceImpl implements MapAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapAnalysisServiceImpl.class);

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    private Long newMapZipId = 0L; //最新地图压缩包id

    private Long oldMapZipId = 0L; //上一批次解压的地图压缩包ID

    @Autowired
    private MapZipService mapZipService;

    @Autowired
    private PointService pointService;

    @Autowired
    private MapInfoService mapInfoService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneMapZipXREFService sceneMapZipXREFService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Override
    public void analysis(JSONObject info) throws Exception {
        //保存地图上传信息
        MapZip mapZip = JSON.parseObject(JSON.toJSONString(info), MapZip.class);
        mapZip.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapZip.setCreateTime(new Date());
        //预设为空串，防止因数据库设置为非空字段导致保存出错
        mapZip.setSceneName("");
        mapZipService.save(mapZip);
        //保存当前的地图压缩包ID
        newMapZipId = mapZip.getId();
        Object sceneMapObject = info.get(Constant.SCENE_MAP_NAME);

        List<String> sceneNames = new ArrayList<>();
        if (sceneMapObject != null) {
            Map<String, List<String>> sceneMapNameMap = (Map<String, List<String>>) sceneMapObject;
            sceneNames = saveSceneMapZipXREF(sceneMapNameMap);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < sceneNames.size(); i++) {
            stringBuffer.append(sceneNames.get(i)).append(",");
        }
        mapZip.setSceneName(stringBuffer.toString());
        mapZipService.update(mapZip);
        //文件不存在新增，有则更新
        File saveFile = unzipMapZipFile(mapZip);
        if (saveFile.exists()) {
            analysisFile(saveFile, sceneNames, mapZip);
        }
    }

    @Override
    public File unzipMapZipFile(MapZip mapZip) {
        //解压文件夹到固定路径
        File saveFile = FileUtils.getFile(DOWNLOAD_HOME, SearchConstants.FAKE_MERCHANT_STORE_ID + "", Constant.FILE_UPLOAD_TYPE_MAP);
        boolean result = ZipUtils.unzip(DOWNLOAD_HOME + mapZip.getFilePath(), saveFile.getAbsolutePath(), false);
        if (!result) {
            LOGGER.info("地图文件解压失败");
            throw new RuntimeException(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_service_impl_MapAnalysisServiceImpl_java_DTWJJYSB"));
        }
        LOGGER.info("地图文件解压成功，保存地址 path=" + saveFile.getAbsolutePath());
        return saveFile;
    }

    @Override
    public void analysisFile(File mapFilePath, List<String> sceneNames, MapZip mapZip) throws Exception {
        //TODO   勿删
//        //更改当前数据库地图数据的状态，改为已删除状态
//        mapInfoService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID ,oldMapZipId, Constant.DELETE);
//        //更改数据库中导航目标点删除状态，改为已删除
//        pointService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID, oldMapZipId, Constant.DELETE);
//        //更改数据库中关联关系表状态，改为已删除
//        sceneMapZipXREFService.updateDeleteFlag(oldMapZipId, Constant.DELETE);

        String deviceId = mapZip.getDeviceId();
        Long mapZipId = mapZip.getId();
        LOGGER.info("解析导航目标点文件，地图文件地址=" + mapFilePath);
        if (!mapFilePath.exists()) {
            return;
        }
        Date createTime = new Date();
        //场景文件夹
        for (int i = 0; i < sceneNames.size(); i++) {
            String sceneName = sceneNames.get(i);
            File sceneDir = new File(mapFilePath.getAbsolutePath() + File.separator + sceneName);

            //解析地图文件
            analysisMapFile(sceneName, sceneDir.getAbsolutePath(), deviceId, mapZipId, createTime);
            //解析导航点文件
            analysisPointFile(sceneName, sceneDir.getAbsolutePath(), mapZipId, createTime);
        }
        //TODO   勿删
//        LOGGER.info("解析完成");
//        //删除状态为DELETE的地图信息
//        mapInfoService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.DELETE);
//        //删除状态为DELETE的导航点信息
//        pointService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.DELETE);
//        sceneMapZipXREFService.delete(Constant.DELETE);
    }

    /**
     * 解析地图文件
     */
    private void analysisMapFile(String sceneName, String sceneDir, String deviceId, Long mapZipId, Date createTime) throws Exception {
        LOGGER.info("开始解析地图文件");
        File mapFilePath = FileUtils.getFile(sceneDir, Constant.MAP_FILE_PATH);
        if (!mapFilePath.exists()) {
            LOGGER.info("地图文件夹不存在。sceneName=" + sceneName + ", sceneDir=" + sceneDir);
            return;
        }

        File[] mapFiles = mapFilePath.listFiles();
        if (null == mapFiles){
            return;
        }
        for (File file : mapFiles) {
            String mapName = file.getName().substring(0, file.getName().lastIndexOf("."));

            Map map = readFileYAML(file);
            if (null == map || map.isEmpty()) {
                continue;
            }
            //根据场景名和地图名查询数据
            List<MapInfo> mapInfoList = mapInfoService.getMapInfo(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            MapInfo mapInfoDB = null;
            if (mapInfoList != null && mapInfoList.size() > 0) {
                mapInfoDB = mapInfoList.get(0);
            }

            MapInfo mapInfo = new MapInfo();
            mapInfo.setMapName(mapName);
            mapInfo.setSceneName(sceneName);
            mapInfo.setMapZipId(mapZipId);
            mapInfo.setDeviceId(deviceId);
            mapInfo.setRos(JSON.toJSONString(map));
            mapInfo.setCreateTime(createTime);

            File pgmFile = new File(mapFilePath.getAbsolutePath(), map.get("image").toString());
            String convertPGMFilePath = convertFile(pgmFile);
            mapInfo.setPngImageLocalPath(convertPGMFilePath.replace(DOWNLOAD_HOME, ""));//保存相对路径
            mapInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            CacheInfoManager.removeMapOriginalCache(FileUtils.parseMapAndSceneName(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID));

            //没有则新增
            if(mapInfoDB == null) {
                mapInfoService.save(mapInfo);
            }
            //有则更新
            else {
                mapInfo.setId(mapInfoDB.getId());
                mapInfo.setFloor(mapInfoDB.getFloor());
                mapInfo.setMapAlias(mapInfoDB.getMapAlias());
                mapInfo.setLogicFloor(mapInfoDB.getLogicFloor());
                mapInfo.setPngDesigned(mapInfoDB.getPngDesigned());
                mapInfoService.update(mapInfo);
            }

            //查询是否有绑定的云端场景，如果有，则更改状态，提示场景需要更新关联的地图
            sceneService.checkSceneIsNeedToBeUpdated(sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID + "");
        }
    }

    /**
     * 解析导航目标点文件
     */
    private void analysisPointFile(String sceneName, String sceneDir, Long mapZipId, Date createTime) throws IllegalAccessException {
        LOGGER.info("开始解析导航目标点文件");
        File pointFilePath = FileUtils.getFile(sceneDir, Constant.POINT_FILE_PATH);
        if (!pointFilePath.exists()) {
            return;
        }
        File[] pointFiles = pointFilePath.listFiles();
        if (null == pointFiles){
            return;
        }
        for (File file : pointFiles) {
            String mapName = file.getName().substring(0, file.getName().lastIndexOf("."));
            Map map = readFileYAML(file);
            if (null == map || map.isEmpty()) {//判断是否为空文件
                continue;
            }
            Iterator iterator = map.entrySet().iterator();
            //遍历导航点
            while (iterator.hasNext()) {
                MapPoint mapPoint = new MapPoint();
                mapPoint.setMapName(mapName);
                mapPoint.setSceneName(sceneName);
                mapPoint.setMapZipId(mapZipId);
                Map.Entry entry = (Map.Entry) iterator.next();
                mapPoint.setPointName(entry.getKey().toString());
                Map valueMap = (Map) entry.getValue();
                //通过反射设置属性值
                Field[] fields = MapPoint.class.getDeclaredFields();
                for (Field field : fields) {
                    if (valueMap.containsKey(field.getName())) {
                        field.setAccessible(true);
                        field.set(mapPoint, valueMap.get(field.getName()));
                }
            }
            mapPoint.setCreateTime(createTime);
            mapPoint.setMapPointTypeId(Integer.parseInt(valueMap.get("type").toString()));
            mapPoint.setPointAlias(valueMap.get("alias").toString());
            mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            IndustrialControlPointType type = IndustrialControlPointType.getType(mapPoint.getMapPointTypeId());
            mapPoint.setICPointType(type != null ? type.getCaption() : "");
            //设置云端类型为未定义
            mapPoint.setCloudMapPointTypeId(MapPointType.UNDEFINED.getCaption());

            //根据场景名，地图名，点名称去数据库查询是否有点，如果有则更新，没有则新增
//            List<MapPoint> mapPointListDB = pointService.findByName(mapPoint.getPointName(), sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            List<MapPoint> mapPointListDB = pointService.listBySceneMapXYTH(sceneName, mapName,
                        mapPoint.getX(),mapPoint.getY(),mapPoint.getTh(),
                        MapPointType.UNDEFINED);
            if (mapPointListDB != null && mapPointListDB.size() > 0) {
                    mapPoint.setId(mapPointListDB.get(0).getId());
                    pointService.update(mapPoint);
                } else {
                    pointService.save(mapPoint);
                }
            }
        }
    }

    // 读取YAML配置文件
    private Map readFileYAML(File file) {
        try {
            String name = file.getName();
            if (name.endsWith(".yaml")) {
                Yaml yaml = new Yaml();
                //也可以将值转换为Map
                return (Map) yaml.load(new FileInputStream(file));
            }
        } catch (Exception e) {
            LOGGER.error("读取YAML配置文件,path=" + file.getAbsolutePath(), e);
        }
        return new HashMap<>();
    }

    // pgm转png
    private String pgmConvertPng(File file) {
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return "";
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                LOGGER.info("Dir pgmConvertPng==>" + f.getAbsolutePath());
                pgmConvertPng(f);
            } else {
                return this.convertFile(f);
            }
        }
        return "";
    }

    private String convertFile(File f) {
        if (f.getName().contains(".pgm")) {
            String pngFilePath = f.getAbsolutePath().replace(".pgm", ".png");
            String cmd = "convert " + f.getAbsolutePath() + " " + pngFilePath;
            LOGGER.info("file pgmConvertPng==>" + f.getAbsolutePath());
            Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
            try {
                LOGGER.info("cmd ==========>" + cmd);
                Process p = run.exec(cmd);// 启动另一个进程来执行命令
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
                String lineStr;
                while ((lineStr = inBr.readLine()) != null){
                    //获得命令执行后在控制台的输出信息
                    LOGGER.info(lineStr);// 打印输出信息
                }
                //检查命令是否执行失败。
                if (p.waitFor() != 0) {
                    //p.exitValue()==0表示正常结束，1：非正常结束
                    if (p.exitValue() == 1){
                        LOGGER.error("命令执行失败! cmd = " + cmd);
                    }
                }
                inBr.close();
                in.close();
                return pngFilePath;
            } catch (Exception e) {
                LOGGER.error("convertFile error", e);
            }
        }
        return "";
    }

    /**
     * 地图。导航点解析出错，回滚所有项
     */
    public void callBack() {
        //回滚压缩文件
        MapZip mapZip = mapZipService.getMapZip(newMapZipId - 1);
        File zipFile = unzipMapZipFile(mapZip);
        if (zipFile.exists()) {
            LOGGER.info("地图压缩包回滚成功，回滚导航点和地图数据");
            //回滚地图信息
            mapInfoService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.NORMAL); //删除新增的正常数据
            mapInfoService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID, oldMapZipId, Constant.NORMAL); //回滚上一批次数据
            //回滚导航目标点
            pointService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.NORMAL); //删除新增的正常数据
            pointService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID, oldMapZipId, Constant.NORMAL); //回滚上一批次数据

            //回滚关联关系数据
            sceneMapZipXREFService.delete(newMapZipId); //删除新增的正常数据
            sceneMapZipXREFService.updateDeleteFlag(oldMapZipId, Constant.NORMAL); //回滚上一批次数据
        }
    }

    /**
     * 保存场景，地图和地图压缩包的关联关系
     *
     * @param sceneMapNameMap
     */
    private List<String> saveSceneMapZipXREF(Map<String, List<String>> sceneMapNameMap) {
        List<String> sceneNames = new ArrayList<>();
        Iterator iterator = sceneMapNameMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();
            String sceneName = entry.getKey();
            sceneNames.add(sceneName);
            List<String> mapNameList = entry.getValue();
            for (String mapName : mapNameList) {
                SceneMapZipXREF sceneMapZipXREF = new SceneMapZipXREF();
                sceneMapZipXREF.setMapName(mapName);
                sceneMapZipXREF.setSceneName(sceneName);
                sceneMapZipXREF.setMapZipId(newMapZipId);
                //如果数据为0，继续查询
                long lastMapZipId = sceneMapZipXREFService.getMapZipId(sceneName, mapName);
                if (lastMapZipId != 0L) {
                    oldMapZipId = lastMapZipId;
                }
                sceneMapZipXREFService.save(sceneMapZipXREF);
            }
        }
        return sceneNames;
    }
}
