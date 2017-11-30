package cn.muye.base.service.imp;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.FileResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.ZipUtils;
import cn.mrobot.utils.ajax.AjaxResponse;
import cn.muye.base.service.FileUploadService;
import cn.muye.publisher.AppSubService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import edu.wpi.rail.jrosbridge.Topic;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jelynn
 * Created by Jelynn on 2017/7/8.
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServiceImpl.class);
    public final static String PREFIX = "http://"; //前缀
    private static final String MAP_UPLOAD_SUCCESS = "1";
    private static final String MAP_UPLOAD_FAIL = "-1";

    @Value("${goor.server.ip}")
    public String ip; //正式服务器地址

    @Value("${goor.server.upload.url}")
    public String UPLOAD_URL; //上传大文件的接口地址

    @Value("${goor.server.exist.url}")
    public String EXIST_URL; //判断文件是否存在

    private final Lock lock = new ReentrantLock();

    @Value("${local.robot.SN}")
    private String deviceId;

    @Value("${server.mapPath}")
    private String MAP_PATH;

    @Value("${server.mapPath_temp}")
    private String mapPathTemp;

    @Autowired
    private AppSubService appSubService;

    @Override
    public void uploadMapFile(String uuid) {
        LOGGER.error("开始上传文件， path= " + MAP_PATH + " REMOTE_URL = " + PREFIX + ip);
        File uploadFile = new File(MAP_PATH);
        if (!checkUploadDir(uploadFile)){
            sendTopic(MAP_UPLOAD_SUCCESS, uuid, "地图文件夹不存在或为空");
            return;
        }
        try {
            Map<String, List<String>> sceneMapNames = getSceneMapName();
            zipDirAndUpload(uploadFile, uuid, sceneMapNames);
        }catch (Exception e){
            LOGGER.info("地图上传失败");
            sendTopic(MAP_UPLOAD_FAIL, uuid, "地图上传失败");
            return;
        }
    }

    @Override
    public void uploadMapFile(String uuid, String topicData) {
        if (StringUtil.isBlank(topicData)){
            LOGGER.info("地图上传场景名为空");
            sendTopic(MAP_UPLOAD_FAIL, uuid, "地图上传场景名为空");
            return;
        }
        JSONObject topicObject = JSONObject.parseObject(topicData);
        String sceneNames = topicObject.getString(TopicConstants.SCENE_NAME);
        List<String> sceneNameList = JSONArray.parseArray(sceneNames, String.class);

        try {
            File uploadFile = null;
            Map<String, List<String>> sceneMapNames = new HashMap<>();
            if (sceneNameList.size() == 1){
                //如果上传单个场景地图，以场景名创建一个文件夹，将该场景地图复制到新建文件夹，压缩上传， 以保持上传zip文件层级一致
                String sceneName = sceneNameList.get(0);
                uploadFile = copySceneMapFileToTemp(sceneName, sceneName, uuid);
                sceneMapNames = getSceneMapName(sceneName);
            }else if (sceneNameList.size() > 1){
                //如果上传多个场景地图，将多个场景地图压缩到一个zip包上传
                String zipDirName = getMultiSceneZipDirName(sceneNameList);
                uploadFile = copySceneMapFileToTemp(sceneNameList, zipDirName, uuid);
                sceneMapNames = getSceneMapName(sceneNameList);
            }
            zipDirAndUpload(uploadFile, uuid, sceneMapNames);
        }catch (Exception e){
            LOGGER.info("地图上传失败");
            sendTopic(MAP_UPLOAD_FAIL, uuid, "地图上传失败");
            return;
        }
    }

    @Override
    public void sendTopic(String errorCode, String uuid, String message) {
        try {
            //封装数据，通知Artmis地图上传成功
            SlamBody slamBody = new SlamBody();
            slamBody.setErrorCode(errorCode);
            slamBody.setPubName(TopicConstants.AGENT_LOCAL_MAP_UPLOAD);
            slamBody.setUuid(uuid);
            slamBody.setMsg(message);
            appSubService.sendTopic(TopicConstants.AGENT_PUB, TopicConstants.TOPIC_TYPE_STRING, slamBody);
        } catch (Exception e) {
            LOGGER.error("FileUpladService sendTopic Exception", e);
        }
    }

    private void zipDirAndUpload(File uploadFile, String uuid, Map<String, List<String>> sceneMapNames){
        if (lock.tryLock()) {
            try {
                if (uploadFile.isDirectory()) {
                    uploadFile = zipDirectory(uploadFile);
                }

                if (uploadFile.length() > 500 * 1024 * 1024) {
                    LOGGER.info("文件" + uploadFile.getName() + "大于500M，无法上传");
                    sendTopic(MAP_UPLOAD_FAIL, uuid, "文件过大（超过500M）");
                    return;
                }
                LOGGER.info("压缩成功，压缩文件地址，path= " + uploadFile.getAbsolutePath());

                FileResult fileResult = checkFileIsExistInCloud(uploadFile);
                if (null == fileResult){
                    sendTopic(MAP_UPLOAD_SUCCESS, uuid, "地图上传失败");
                    return;
                }

                if (fileResult.isExist()) {
                    sendTopic(MAP_UPLOAD_SUCCESS, uuid, "文件已经存在云端");
                    uploadFile.delete();
                    return;
                }
                long jumpSize = 0;
                if (fileResult.isTemp()) {
                    jumpSize = fileResult.getSize();
                }
                AjaxResponse resp = executeUploadFile(uploadFile, jumpSize, sceneMapNames);
                LOGGER.info("上传状态 status = " + resp.getStatus());
                if (resp.getStatus() == AjaxResponse.RESPONSE_STATUS_SUCCESS) {
                    //上传成功
                    uploadFile.delete();
                    LOGGER.info("上传成功，删除文件，发送topic");
                    sendTopic(MAP_UPLOAD_SUCCESS, uuid, "地图上传成功");
                } else {
                    LOGGER.info("上传失败，发送topic");
                    sendTopic(MAP_UPLOAD_FAIL, uuid, "地图上传失败");
                }
            } catch (HttpHostConnectException e) {
                sendTopic(MAP_UPLOAD_FAIL, uuid, "未连上服务器");
                LOGGER.error("socket超时", e);
            } catch (ClientProtocolException | SocketTimeoutException e) {
                sendTopic(MAP_UPLOAD_FAIL, uuid, "socket超时");
                LOGGER.error("socket超时", e);
            } catch (Exception e) {
                sendTopic(MAP_UPLOAD_FAIL, uuid, "上传错误");
                LOGGER.error("上传错误", e);
            } finally {
                this.lock.unlock();
            }
        }
    }

    private File copySceneMapFileToTemp(List<String> sceneNameList, String tempDirName, String uuid) throws Exception {
        File file = null;
        for (String sceneName : sceneNameList){
            file = copySceneMapFileToTemp(sceneName, tempDirName, uuid);
        }
        return file;
    }

    private File copySceneMapFileToTemp(String sceneName, String tempDirName, String uuid) throws Exception {
        File sceneNameDir = new File(MAP_PATH + File.separator + sceneName);
        if (!checkUploadDir(sceneNameDir)){
            sendTopic(MAP_UPLOAD_SUCCESS, uuid, "地图文件夹不存在或为空");
            return null;
        }

        File zipDirNameTemp = new File(mapPathTemp + File.separator + tempDirName);
        if (!zipDirNameTemp.exists()){
            zipDirNameTemp.mkdirs();
        }
        //复制场景文件夹中的文件到临时文件夹
        FileUtils.copyDirectoryToDirectory(sceneNameDir, zipDirNameTemp);
        return zipDirNameTemp;
    }

    private String getMultiSceneZipDirName(List<String> sceneNameList) {
        StringBuilder stringBuffer = new StringBuilder();
        int size = sceneNameList.size();
        for (int i = 0; i < size ; i++){
            stringBuffer.append(sceneNameList.get(i));
            if (i != size -1){
                stringBuffer.append("_");
            }
        }
        return stringBuffer.toString();
    }

    private boolean checkUploadDir(File file){
        if (!file.exists()) {
            LOGGER.error("文件夹不存在, path= " + MAP_PATH);
            return false;
        }
        if (file.isDirectory() && file.listFiles().length <= 0) {
            LOGGER.error("文件夹为空, path= " + MAP_PATH);
            return false;
        }
        return true;
    }

    private FileResult checkFileIsExistInCloud(File uploadFile ) throws IOException {
        String REMOTE_URL = PREFIX + ip;
        Map<String, String> params = Maps.newHashMap();
        params.put("fileName", uploadFile.getName());
        params.put("type", Constant.FILE_UPLOAD_TYPE_MAP);
        //查询文件是否存在
        String jsonResult = HttpClientUtil.executePost(null, REMOTE_URL + EXIST_URL, params, null, null, null, null, null, true);
        JSONObject object = JSON.parseObject(jsonResult);
        Integer code = object.getInteger("code");

        if (null != code && (code == Constant.ERROR_CODE_NOT_AUTHORIZED || code == Constant.ERROR_CODE_NOT_LOGGED || code == AjaxResult.CODE_SYSTEM_ERROR)) {
            LOGGER.info("上传失败，code= " + code + ", message=" + object.getString("message"));
            return null;
        }

        FileResult fileResult = JSON.parseObject(jsonResult, FileResult.class);
        if (fileResult.getStatus() != 0) {
            return null;
        }
        return fileResult;
    }

    private AjaxResponse executeUploadFile(File uploadFile, Long jumpSize, Map<String, List<String>> sceneMapNames) throws Exception {
        //获取上传信息
        String REMOTE_URL = PREFIX + ip;
        JSONObject otherInfo = new JSONObject();
        otherInfo.put("robotPath", MAP_PATH);
        otherInfo.put(Constant.SCENE_MAP_NAME, sceneMapNames);
        otherInfo.put("deviceId", deviceId);
        LOGGER.info("开始上传，path= " + uploadFile.getAbsolutePath());
        String uri = REMOTE_URL + UPLOAD_URL + "?fileName=" + uploadFile.getName() + "&type=" + Constant.FILE_UPLOAD_TYPE_MAP;
        String result = HttpClientUtil.executeUploadFile(null, uri, uploadFile.getPath(), jumpSize, null, true, JSON.toJSONString(otherInfo));
        AjaxResponse resp = JSON.parseObject(result, AjaxResponse.class);
        return resp;
    }

    /**
     * 压缩文件夹
     *
     * @param file
     * @return
     */
    private File zipDirectory(File file) throws Exception {
        LOGGER.info("开始压缩");
        //压缩文件夹
        String lastDirName = file.getName();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateStr = format.format(new Date());
        String zipFileName = lastDirName + "_" + dateStr + TopicConstants.ZIP_FILE_SUFFIX;

        String uploadFilePath = mapPathTemp + File.separator + zipFileName;
        File zipFile = new File(uploadFilePath);
        zipFile.deleteOnExit(); //如果存在同名文件，则删除
        LOGGER.info("压缩文件夹，zipFileName = " + zipFileName);
        boolean zipResult = ZipUtils.zip(file.getAbsolutePath(), mapPathTemp, zipFileName);
        if (!zipResult) {
            LOGGER.error("文件夹压缩失败，停止上传");
            return null;
        }
        return new File(mapPathTemp, zipFileName);
    }

    /**
     * 获取场景和地图名，返回值为map,key为场景名，value为场景对应的地图名，多个地图名通过下划线（_）拼接
     *
     * @return
     */
    private Map<String, List<String>> getSceneMapName() throws Exception {
        File file = new File(MAP_PATH);
        Map<String, List<String>> sceneMapNames = new HashMap<>();
        if (!file.exists()) {
            return sceneMapNames;
        }
        File[] sceneDirs = file.listFiles();
        for (int i = 0; i < sceneDirs.length; i++) {
            File sceneDir = sceneDirs[i];
            Map<String, List<String>> map = getSceneMapName(sceneDir.getName());
            sceneMapNames.putAll(map);
        }
        return sceneMapNames;
    }

    private Map<String, List<String>> getSceneMapName(List<String> sceneNameList) throws Exception {
        Map<String, List<String>> sceneMapNames = new HashMap<>();
       for (String sceneName : sceneNameList){
           Map<String, List<String>> map = getSceneMapName(sceneName);
           sceneMapNames.putAll(map);
       }
       return sceneMapNames;
    }

    private Map<String, List<String>> getSceneMapName(String sceneName) throws Exception {
        File sceneFile = new File(MAP_PATH + File.separator + sceneName + File.separator + Constant.MAP_FILE_PATH);
        Map<String, List<String>> map = new HashMap<>();
        if (!sceneFile.exists()) {
            return map;
        }
        File[] mapFiles = sceneFile.listFiles();
        List<String> mapNameList = new ArrayList<>();
        for (int j = 0; j < mapFiles.length; j++) {
            File mapFile = mapFiles[j];
            if (mapFile.getName().endsWith(Constant.ROS_MAP_FILE_SUFFIX)) {
                String mapFileName = mapFile.getName();
                mapNameList.add(mapFileName.substring(0, mapFileName.indexOf(Constant.ROS_MAP_FILE_SUFFIX)));
            }
        }
        map.put(sceneName, mapNameList);
        return map;
    }
}
