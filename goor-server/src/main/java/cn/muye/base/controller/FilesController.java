package cn.muye.base.controller;

import cn.mrobot.bean.FileResult;
import cn.mrobot.bean.FileUpload;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.exception.MapAnalysisException;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.ajax.AjaxResponse;
import cn.muye.area.map.service.MapAnalysisService;
import cn.muye.base.bean.SearchConstants;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private FileUploadService fileUploadService;

    @Autowired
    private MapAnalysisService mapAnalysisService;

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
        JSONObject otherInfoObject = JSON.parseObject(URLDecoder.decode(otherInfo));
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
            String relativePath = real.getPath().replace(DOWNLOAD_HOME, "");
            LOGGER.info("上传成功，文件保存路径，path = " + real.getPath());
            // 传输成功后，添加至数据库
            long uploadFileId = saveUploadFile(relativePath, fileName, temp.length());
            otherInfoObject.put("filePath", relativePath);
            otherInfoObject.put("fileUploadId", uploadFileId);

            return AjaxResponse.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResponse.failed(-1, "文件上传失败");
        } finally {
            try {
                in.close();
                if (tempRandAccessFile != null) {
                    tempRandAccessFile.close();
                }
                if (isSuccess) {
                    temp.renameTo(real);
                    new Thread(() ->{
                        try {
                            analysis(type, otherInfoObject);
                        } catch (Exception e) {
                            LOGGER.error("解压地图包出错", e);
                        }
                    }).start();
                }
            } catch (IOException e) {
                return AjaxResponse.failed(-1, "文件上传失败");
            }catch (Exception e) {
                return AjaxResponse.failed(-1, e.getMessage());
            }
        }
    }

    private long saveUploadFile(String relativePath ,String fileName, long length){
        FileUpload uploadFile = fileUploadService.getByName(fileName);
        if (uploadFile != null) {
            uploadFile.setUpdateTime(new Date());
            uploadFile.setLength(length);
            uploadFile.setStatus(Constant.FILE_OK);
            fileUploadService.update(uploadFile);
        } else {
            FileUpload newFile = new FileUpload();
            newFile.setCreateTime(new Date());
            newFile.setUpdateTime(newFile.getCreateTime());
            newFile.setLength(length);
            newFile.setName(fileName);
            newFile.setStatus(Constant.FILE_OK);
            newFile.setPath(relativePath);
            fileUploadService.save(newFile);
            uploadFile = newFile;
        }
        return uploadFile.getId();
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

    public void analysis(String type, JSONObject info) throws Exception {
        if (Constant.FILE_UPLOAD_TYPE_MAP.equals(type)) {
            //解析上传的地图
            try {
                mapAnalysisService.analysis(info);
            } catch (Exception e) {
//                mapAnalysisService.callBack();
                throw new MapAnalysisException("地图解析失败，数据回滚");
            }

        }
    }
}