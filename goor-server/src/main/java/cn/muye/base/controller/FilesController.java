package cn.muye.base.controller;

import cn.mrobot.bean.FileResult;
import cn.mrobot.bean.FileUpload;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.ZipUtils;
import cn.mrobot.utils.ajax.AjaxResponse;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.map.service.MapZipService;
import cn.muye.area.point.service.PointService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.FileUploadService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Martin on 2016/4/21.
 */
@Controller
public class FilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesController.class);

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    @Autowired
    private MapZipService mapZipService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private PointService pointService;

    @Autowired
    private MapInfoService mapInfoService;

    private static final String RESOURCE_TYPE_DIR = "default";
    private static final String RESOURCE_TYPE_FILE = "file";

    /**
     * 上传文件(其他系统上传文件的时候需要添加type参数，值为file)
     *
     * @param file
     * @param path
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = {"services/public/files/upload"})
    @ResponseBody
    public AjaxResponse updateResource(@RequestParam("file") MultipartFile file, String path, @RequestParam(value = "deviceId", required = false) String deviceId, HttpServletRequest request) throws IllegalStateException, IOException {
        AjaxResponse resp = AjaxResponse.success();
        try {
            String type = request.getParameter("type");
            if (type == null) {
                return AjaxResponse.failed(-1, "参数错误");
            }
            if (StringUtil.isNullOrEmpty(deviceId)) {
                return AjaxResponse.failed(-2, "设备编号不能为空");
            }
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + deviceId + File.separator + path);
            if (type.equals(RESOURCE_TYPE_DIR)) {
                dest.mkdirs();
            } else if (!file.isEmpty() && type.equals(RESOURCE_TYPE_FILE)) {
                LOGGER.info("createResource dest.path ={} ", dest.getPath());
                dest.mkdirs();
                String fileName = file.getOriginalFilename();
                dest = FileUtils.getFile(dest.getPath() + File.separator + fileName);
                LOGGER.info("createResource dest.path with fileName ={} ", dest.getPath());
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                file.transferTo(dest);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            resp = AjaxResponse.failed(-1);
            resp.setErrorString("出错");
        }
        return resp;
    }

    /**
     * 判断资源文件是否存在
     *
     * @param path
     * @param deviceId
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = {"admin/files/exist"})
    @ResponseBody
    public AjaxResponse getIsResourceExist(String path, @RequestParam(value = "deviceId", required = false) String deviceId, HttpServletRequest request) throws IllegalStateException, IOException {
        AjaxResponse resp = AjaxResponse.success();

        if (StringUtil.isNullOrEmpty(deviceId)) {
            return AjaxResponse.failed(-2, "设备编号不能为空");
        }

        File dest = FileUtils.getFile(DOWNLOAD_HOME, deviceId, File.separator + path);
        Map entry = new HashMap();
        if (dest.exists()) {
            entry.put(Constant.FILE_IS_EXIST, true);
        } else {
            entry.put(Constant.FILE_IS_EXIST, false);
        }
        resp.addDataEntry(entry);
        return resp;
    }

    /**
     * 上传大个文件 支持断点续传
     *
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "services/public/files/largeUpload", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse uploadLargeFile(HttpServletRequest request) throws IllegalStateException, IOException {
        String fileName = request.getParameter("fileName");
        String type = request.getParameter("type");
        String otherInfo = request.getHeader("otherInfo");
        JSONObject otherInfoObject = JSON.parseObject(otherInfo);
        otherInfoObject.put("fileName", fileName);

        InputStream in = request.getInputStream();
        RandomAccessFile tempRandAccessFile = null;
        long length = request.getContentLength();
        File real = null;
        File temp = null;
        boolean isSuccess = false;
        try {
            File upload = FileUtils.getFile(DOWNLOAD_HOME, SearchConstants.FAKE_MERCHANT_STORE_ID + "", "upload", type);
            upload.mkdirs();
            real = FileUtils.getFile(upload.getPath(), File.separator + fileName);
            temp = FileUtils.getFile(upload.getPath(), File.separator + fileName + ".tmp");
            if (real.exists()) {
                return AjaxResponse.success();
            } else {
                long needSkipBytes = 0;
                if (temp.exists()) {
                    //续一哈
                    needSkipBytes = temp.length();
                } else {
                    temp.createNewFile();
                }
                System.out.println("跳过字节数为：" + needSkipBytes);
                //in.skip(needSkipBytes);
                tempRandAccessFile = new RandomAccessFile(temp, "rw");
                tempRandAccessFile.seek(needSkipBytes);
                byte[] buffer = new byte[4096];
                int l;
                if (length < 0L) {
                    while ((l = in.read(buffer)) != -1) {
                        tempRandAccessFile.write(buffer, 0, l);
                    }
                } else {
                    for (long remaining = length - needSkipBytes; remaining > 0L; remaining -= (long) l) {
                        l = in.read(buffer, 0, (int) Math.min(4096L, remaining));
                        if (l == -1) {
                            break;
                        }
                        tempRandAccessFile.write(buffer, 0, l);
                    }
                }
                isSuccess = true;
            }
            //获取文件的相对路径
            String relativePath = real.getPath().replace(DOWNLOAD_HOME,"");
            // 传输成功后，添加至数据库
            FileUpload uploadFile = fileUploadService.getByName(fileName);
            if (uploadFile != null) {
                uploadFile.setUpdateTime(new Date());
                uploadFile.setLength(temp.length());
                uploadFile.setStatus(Constant.FILE_OK);
                //uploadFile.setMd5(FileValidCreateUtil.fileMD5(real.getAbsolutePath()));
                fileUploadService.update(uploadFile);
            } else {
                FileUpload newFile = new FileUpload();
                newFile.setCreateTime(new Date());
                newFile.setUpdateTime(newFile.getCreateTime());
                newFile.setLength(temp.length());
                newFile.setName(fileName);
                newFile.setStatus(Constant.FILE_OK);
                newFile.setPath(relativePath);
                // newFile.setMd5(FileValidCreateUtil.fileMD5(real.getAbsolutePath()));
                fileUploadService.save(newFile);
                uploadFile = newFile;
            }

            otherInfoObject.put("filePath", relativePath);
            otherInfoObject.put("fileUploadId", uploadFile.getId());

            return AjaxResponse.success();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResponse.failed(-1);
        } finally {
            try {
                in.close();
                if (tempRandAccessFile != null) {
                    tempRandAccessFile.close();
                }
                if (isSuccess) {
                    temp.renameTo(real);
                    analysis(type, otherInfoObject);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判定是否已存在此文件
     *
     * @param name 文件名称
     * @param md5  文件md5
     */
    @RequestMapping(value = "services/public/files/isExistFile", method = RequestMethod.POST)
    @ResponseBody
    public FileResult getIsExistFile(@RequestParam(value = "fileName") String name,
                                     @RequestParam(value = "type") String type,
                                     @RequestParam(value = "md5", required = false) String md5) {
        FileResult result = new FileResult();
        try {
            FileUpload existFile = fileUploadService.getByName(name);
            if (existFile != null) {
                //若存在 直接
                result.setExist(true);
            } else {
                //若不存在,是否存在临时文件
                result.setExist(false);
                File upload = FileUtils.getFile(DOWNLOAD_HOME, SearchConstants.FAKE_MERCHANT_STORE_ID + "", "upload", type);
                File temp = FileUtils.getFile(upload.getAbsolutePath(), name + ".tmp");
                if (temp.exists() && temp.isFile()) {
                    result.setTemp(true);
                    result.setSize(temp.length());
                } else {
                    result.setTemp(false);
                }
            }
            result.setStatus(0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new FileResult(1);
        }
    }

    /**
     * 处理文件上传的其他信息
     *
     * @param type
     * @param info
     */
    private void analysis(String type, JSONObject info) {
        if (Constant.FILE_UPLOAD_TYPE_MAP.equals(type)) {
            //保存地图上传信息
            MapZip mapZip = JSON.parseObject(JSON.toJSONString(info), MapZip.class);
            mapZip.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            mapZip.setCreateTime(new Date());
            mapZipService.save(mapZip);
            //解压文件夹到固定路径
            try {
                File saveFile = FileUtils.getFile(DOWNLOAD_HOME, SearchConstants.FAKE_MERCHANT_STORE_ID + "", mapZip.getDeviceId());
                if (saveFile.exists()) {
                    FileUtils.deleteDir(saveFile);
                }
                saveFile.mkdirs();
                ZipUtils.unzip(DOWNLOAD_HOME + mapZip.getFilePath(), saveFile.getAbsolutePath(), false);
                LOGGER.info("地图文件解压成功，保存地址 path=" + saveFile.getAbsolutePath());
                //解析地图文件
                analysisFile(saveFile, mapZip);
            } catch (Exception e) {
                LOGGER.error("地图文件解压失败", e);
            }
        }
    }

    public void analysisFile(File mapFilePath, MapZip mapZip) {
        String deviceId = mapZip.getDeviceId();
        Long mapzipId = mapZip.getId();
        LOGGER.info("解析导航目标点文件，地图文件地址=" + mapFilePath);
        if (!mapFilePath.exists()) {
            return;
        }
        //场景文件夹
        File[] sceneFileDirs = mapFilePath.listFiles();
        for (int i = 0; i < sceneFileDirs.length; i++) {
            File sceneDir = sceneFileDirs[i];
            String sceneName = sceneDir.getName();
            try {
                //解析地图文件
                analysisMapFile(sceneName, sceneDir.getAbsolutePath(), deviceId, mapzipId);
                //解析导航点文件
                analysisPointFile(sceneName, sceneDir.getAbsolutePath());
            } catch (IllegalAccessException e) {
                LOGGER.error("解析导航目标点出错", e);
            }
        }
    }

    //解析地图文件
    private void analysisMapFile(String sceneName, String sceneDir, String deviceId, Long mapzipId) throws IllegalAccessException {
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
            //TODO 删除原数据库中的地图数据
            MapInfo mapInfoDB = mapInfoService.getMapInfo(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (mapInfoDB != null) {
                mapInfoService.delete(mapInfoDB);
            }
            mapInfo.setRos(JSON.toJSONString(map));

            File pgmFile = new File(mapFilePath.getAbsolutePath(), map.get("image").toString());
            String convertPGMFilePath = convertFile(pgmFile);
            mapInfo.setPngImageLocalPath(convertPGMFilePath.replace(DOWNLOAD_HOME, ""));//保存相对路径
            mapInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            CacheInfoManager.removeMapOriginalCache(FileUtils.parseMapAndSceneName(mapName, sceneName,SearchConstants.FAKE_MERCHANT_STORE_ID));
            mapInfoService.save(mapInfo);
        }
    }

    //解析导航目标点文件
    private void analysisPointFile(String sceneName, String sceneDir) throws IllegalAccessException {
        File pointFilePath = FileUtils.getFile(sceneDir, Constant.POINT_FILE_PATH);
        if (!pointFilePath.exists()) {
            return;
        }
        File[] pointFiles = pointFilePath.listFiles();
        for (int i = 0; i < pointFiles.length; i++) {
            File file = pointFiles[i];

            String mapName = file.getName().substring(0, file.getName().lastIndexOf("."));
            //删除原数据库中该地图和场景名中的导航目标点
            pointService.delete(sceneName, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);

            Map map = readFileYAML(file);
            if (null == map || map.isEmpty()) {
                continue;
            }
            Iterator iterator = map.entrySet().iterator();
            //遍历导航点
            while (iterator.hasNext()) {
                MapPoint mapPoint = new MapPoint();
                mapPoint.setMapName(mapName);
                mapPoint.setSceneName(sceneName);
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
                mapPoint.setMapPointTypeId(Integer.parseInt(valueMap.get("type").toString()));
                mapPoint.setPointAlias(valueMap.get("alias").toString());
                mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
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
}