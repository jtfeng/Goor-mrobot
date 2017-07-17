package cn.muye.base.service;

import cn.mrobot.bean.FileResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.ZipUtils;
import cn.mrobot.utils.ajax.AjaxResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Jelynn on 2017/7/8.
 */
@Service
public class FileUpladService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpladService.class);
    public final static String PREFIX = "http://"; //前缀

    @Value("${goor.server}")
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

    /**
     * 打包上传地图文件
     */
    public void uploadMapFile() {
        String REMOTE_URL = PREFIX + ip;
        if (lock.tryLock()) {
            try {
                File uploadFile = new File(MAP_PATH);
                if (!uploadFile.exists()) {
                    LOGGER.error("文件不存在, path= " + MAP_PATH);
                    return;
                }
                if (uploadFile.isDirectory()) {
                    //压缩文件夹
                    String lastDirName = uploadFile.getName();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
                    String dateStr = format.format(new Date());
                    String zipFileName = lastDirName + "_" + dateStr + TopicConstants.ZIP_FILE_SUFFIX;

                    String uploadFilePath = uploadFile.getParent() + File.separator + zipFileName;
                    File zipFile = new File(uploadFilePath);
                    zipFile.deleteOnExit(); //如果存在同名文件，则删除

                    boolean zipResult = ZipUtils.zip(MAP_PATH, uploadFile.getParent(), zipFileName);
                    if (!zipResult) {
                        LOGGER.error("文件夹压缩失败，停止上传");
                        return;
                    }
                    uploadFile = new File(uploadFile.getParent(), zipFileName);
                }
                if (uploadFile.length() > 500 * 1024 * 1024) {
                    LOGGER.info("文件" + uploadFile.getName() + "大于500M，无法上传");
                    return;
                }

                Map<String, String> params = Maps.newHashMap();
                params.put("fileName", uploadFile.getName());
                params.put("type", Constant.FILE_UPLOAD_TYPE_MAP);
                //查询文件是否存在
                String jsonResult = HttpClientUtil.executePost(null, REMOTE_URL + EXIST_URL, params, null, null, null, true);
                FileResult fileResult = JSON.parseObject(jsonResult, FileResult.class);
                if (fileResult.getStatus() == 0) {
                    if (!fileResult.isExist()) {
                        long jumpSize = 0;
                        if (fileResult.isTemp()) {
                            jumpSize = fileResult.getSize();
                        }
                        //获取上传信息
                        JSONObject otherInfo = new JSONObject();
                        otherInfo.put("robotPath", MAP_PATH);
                        otherInfo.put("sceneName", getSceneName());
                        otherInfo.put("deviceId", deviceId);
                        String uri = REMOTE_URL + UPLOAD_URL + "?fileName=" + uploadFile.getName() + "&type=" + Constant.FILE_UPLOAD_TYPE_MAP;
                        String result = HttpClientUtil.executeUploadFile(null, uri, uploadFile.getPath(), jumpSize, null, true, JSON.toJSONString(otherInfo));
                        AjaxResponse resp = JSON.parseObject(result, AjaxResponse.class);
                        if (resp.getStatus() == AjaxResponse.RESPONSE_STATUS_SUCCESS) {
                            //上传成功
                            uploadFile.delete();
                        }
                    } else {
                        //假提示上传成功 同时删除本地文件
                        uploadFile.delete();
                    }
                }
            } catch (SocketTimeoutException e) {
                LOGGER.error("socket超时", e);
            } catch (ClientProtocolException e) {
                LOGGER.error("socket超时", e);
            } catch (Exception e) {
                LOGGER.error("上传错误", e);
            } finally {
                this.lock.unlock();
            }
        }
    }

    private String getSceneName() {
        File file = new File(MAP_PATH);
        if (!file.exists()) {
            return "";
        }
        if (file.isFile()) {
            return file.getName();
        }
        File[] files = file.listFiles();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if(name.lastIndexOf(".zip") >= 0 || name.lastIndexOf(".flags") >= 0)
                continue;
            sb.append(files[i].getName()).append(",");
        }
        return sb.toString();
    }
}
