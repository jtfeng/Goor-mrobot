package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.SceneMapZipXREF;
import cn.mrobot.bean.area.point.IndustrialControlPointType;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.ZipUtils;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
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
 * Created by Jelynn on 2017/7/26.
 */
@Service
public class MapAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapAnalysisService.class);

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

    /**
     * 处理文件上传的其他信息
     *
     * @param info
     */
    public void analysis(JSONObject info) throws Exception {
        //保存地图上传信息
        MapZip mapZip = JSON.parseObject(JSON.toJSONString(info), MapZip.class);
        mapZip.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        mapZip.setCreateTime(new Date());
        Object sceneMapObject = info.get(Constant.SCENE_MAP_NAME);

        List<String> sceneNames = new ArrayList<>();
        if (sceneMapObject != null) {
            Map<String, List<String>> sceneMapNameMap = (Map<String, List<String>>) sceneMapObject;
            sceneNames = saveSceneMapZipXREF(sceneMapNameMap);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i  =0; i <sceneNames.size(); i ++ ){
            stringBuffer.append(sceneNames.get(i)).append(",");
        }
        mapZip.setSceneName(stringBuffer.toString());
        mapZipService.save(mapZip);
        newMapZipId = mapZip.getId();  //保存当前的地图压缩包ID

        File saveFile = unzipMapZipFile(mapZip);
        if (saveFile.exists()) {
            analysisFile(saveFile, sceneNames,mapZip);
        }
    }

    /**
     * 解压地图压缩包，返回解压后的文件夹
     *
     * @param mapZip
     * @return
     */
    public File unzipMapZipFile(MapZip mapZip) {
        //解压文件夹到固定路径
        File saveFile = FileUtils.getFile(DOWNLOAD_HOME, SearchConstants.FAKE_MERCHANT_STORE_ID + "", mapZip.getDeviceId());
        if (saveFile.exists()) {
            FileUtils.deleteDir(saveFile);
        }
        saveFile.mkdirs();
        boolean result = ZipUtils.unzip(DOWNLOAD_HOME + mapZip.getFilePath(), saveFile.getAbsolutePath(), false);
        if (!result) {
            LOGGER.info("地图文件解压失败");
            throw new RuntimeException("地图文件解压失败");
        }
        LOGGER.info("地图文件解压成功，保存地址 path=" + saveFile.getAbsolutePath());
        return saveFile;
    }

    /**
     * 解析解压后的文件
     *
     * @param mapFilePath
     * @param mapZip
     * @throws Exception
     */
    public void analysisFile(File mapFilePath, List<String> sceneNames, MapZip mapZip) throws Exception {
        //更改当前数据库地图数据的状态，改为已删除状态
        mapInfoService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID ,oldMapZipId, Constant.DELETE);
        //更改数据库中导航目标点删除状态，改为已删除
        pointService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID, oldMapZipId, Constant.DELETE);
        //更改数据库中关联关系表状态，改为已删除
        sceneMapZipXREFService.updateDeleteFlag(oldMapZipId, Constant.DELETE);

        String deviceId = mapZip.getDeviceId();
        Long mapzipId = mapZip.getId();
        LOGGER.info("解析导航目标点文件，地图文件地址=" + mapFilePath);
        if (!mapFilePath.exists()) {
            return;
        }
        //场景文件夹
        for (int i = 0; i < sceneNames.size(); i++) {
            File sceneDir = new File(mapFilePath.getAbsolutePath() + File.separator +sceneNames.get(i));
            String sceneName = sceneDir.getName();
            //解析地图文件
            analysisMapFile(sceneName, sceneDir.getAbsolutePath(), deviceId, mapzipId);
            //解析导航点文件
            analysisPointFile(sceneName, sceneDir.getAbsolutePath(), mapzipId);
        }
        LOGGER.info("解析完成，删除作废的数据");

        //删除状态为DELETE的地图信息
        mapInfoService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.DELETE);
        //删除状态为DELETE的导航点信息
        pointService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.DELETE);
        sceneMapZipXREFService.delete(Constant.DELETE);
    }

    /**
     * 解析地图文件
     */
    private void analysisMapFile(String sceneName, String sceneDir, String deviceId, Long mapzipId) throws Exception {
        LOGGER.info("开始解析地图文件");
        File mapFilePath = FileUtils.getFile(sceneDir, Constant.MAP_FILE_PATH);
        if (!mapFilePath.exists()) {
            LOGGER.info("地图文件夹不存在。sceneName=" + sceneName + ", sceneDir=" + sceneDir);
            return;
        }

        File[] mapFiles = mapFilePath.listFiles();
        for (int i = 0; i < mapFiles.length; i++) {
            File file = mapFiles[i];
            String mapName = file.getName().substring(0, file.getName().lastIndexOf("."));

            Map map = readFileYAML(file);
            if (null == map || map.isEmpty()) {
                continue;
            }
            //遍历导航点
            MapInfo mapInfo = new MapInfo();
            mapInfo.setMapName(mapName);
            mapInfo.setSceneName(sceneName);
            mapInfo.setMapZipId(mapzipId);
            mapInfo.setDeviceId(deviceId);
            mapInfo.setCreateTime(new Date());
            mapInfo.setRos(JSON.toJSONString(map));

            File pgmFile = new File(mapFilePath.getAbsolutePath(), map.get("image").toString());
            String convertPGMFilePath = convertFile(pgmFile);
            mapInfo.setPngImageLocalPath(convertPGMFilePath.replace(DOWNLOAD_HOME, ""));//保存相对路径
            mapInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            CacheInfoManager.removeMapOriginalCache(FileUtils.parseMapAndSceneName(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID));
            mapInfoService.save(mapInfo);
            //查询是否有绑定的云端场景，如果有，则更改状态，提示场景需要更新关联的地图
            sceneService.checkSceneIsNeedToBeUpdated(sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID + "", Scene.SCENE_STATE.UPDATE_STATE);
        }
    }

    /**
     * 解析导航目标点文件
     */
    private void analysisPointFile(String sceneName, String sceneDir,Long mapzipId) throws IllegalAccessException {
        LOGGER.info("开始解析导航目标点文件");
        File pointFilePath = FileUtils.getFile(sceneDir, Constant.POINT_FILE_PATH);
        if (!pointFilePath.exists()) {
            return;
        }
        File[] pointFiles = pointFilePath.listFiles();
        for (int i = 0; i < pointFiles.length; i++) {
            File file = pointFiles[i];

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
                mapPoint.setMapZipId(mapzipId);
                Map.Entry entry = (Map.Entry) iterator.next();
                mapPoint.setPointName(entry.getKey().toString());
                Map valueMap = (Map) entry.getValue();
                //通过反射设置属性值
                Field[] fields = MapPoint.class.getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    if (valueMap.containsKey(fields[j].getName())) {
                        Field field = fields[j];
                        field.setAccessible(true);
                        field.set(mapPoint, valueMap.get(field.getName()));
                    }
                }
                mapPoint.setCreateTime(new Date());
                mapPoint.setMapPointTypeId(Integer.parseInt(valueMap.get("type").toString()));
                mapPoint.setPointAlias(valueMap.get("alias").toString());
                mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                mapPoint.setICPointType(IndustrialControlPointType.getType(mapPoint.getMapPointTypeId()).getName());
                pointService.save(mapPoint);
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
                Map map = (Map) yaml.load(new FileInputStream(file));
                return map;
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
                while ((lineStr = inBr.readLine()) != null)
                    //获得命令执行后在控制台的输出信息
                    LOGGER.info(lineStr);// 打印输出信息
                //检查命令是否执行失败。
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束
                        LOGGER.error("命令执行失败!");
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
     *
     * @return
     */
    public boolean callBack() {
        //回滚压缩文件
        MapZip mapZip = mapZipService.getMapZip(newMapZipId - 1);
        File zipFile = unzipMapZipFile(mapZip);
        if (zipFile.exists()) {
            LOGGER.info("地图压缩包回滚成功，回滚导航点和地图数据");
            //回滚地图信息
            mapInfoService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.NORMAL); //删除新增的正常数据
            mapInfoService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID,oldMapZipId, Constant.NORMAL); //回滚上一批次数据
            //回滚导航目标点
            pointService.delete(SearchConstants.FAKE_MERCHANT_STORE_ID, Constant.NORMAL); //删除新增的正常数据
            pointService.updateDeleteFlag(SearchConstants.FAKE_MERCHANT_STORE_ID, oldMapZipId, Constant.NORMAL); //回滚上一批次数据

            //回滚关联关系数据
            sceneMapZipXREFService.delete(newMapZipId); //删除新增的正常数据
            sceneMapZipXREFService.updateDeleteFlag(oldMapZipId, Constant.NORMAL); //回滚上一批次数据
            return true;
        }
        return false;
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
            for (int i = 0; i < mapNameList.size(); i++) {
                SceneMapZipXREF sceneMapZipXREF = new SceneMapZipXREF();
                String mapName = mapNameList.get(i);
                sceneMapZipXREF.setMapName(mapName);
                sceneMapZipXREF.setSceneName(sceneName);
                sceneMapZipXREF.setMapZipId(newMapZipId);
                //如果数据为0，继续查询
                Long lastMapZipId = sceneMapZipXREFService.getMapZipId(sceneName, mapName);
                if(lastMapZipId != null && lastMapZipId != 0L){
                    oldMapZipId =lastMapZipId;
                }
                sceneMapZipXREFService.save(sceneMapZipXREF);
            }
        }
        return sceneNames;
    }
}
