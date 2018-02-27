package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.utils.HttpClientUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Selim on 2017/11/15.
 */
@Service
public class FixFilePathServiceImpl implements FixFilePathService {

    private Logger logger = Logger.getLogger(FixFilePathServiceImpl.class);

    //http 请求
    public static final String HTTP_REQUEST = "http://";
    //goor-server端接收上传接口
    public static final String REMOTE_PATH_UPLOAD_URL = "/area/fixpath/upload";

    //文件名称 及 存储位置
    public static final String FILENAME  = "pathes.txt";
    //public static final String PREFIX_URL  = "D:\\path";
    public static final String PREFIX_URL = "/map";

    @Value("${local.robot.SN}")
    private String localRobotSN;

    @Value("${goor.server.ip}")
    private String goorServerIp;

    @Override
    public AjaxResult handleFixFilePath(String sceneName) {
        logger.info("请求的场景名为"+ sceneName);
        String url = PREFIX_URL + File.separator + sceneName + File.separator + FILENAME;
        try {
            Map entry = new HashMap();
            entry.put("sendId", localRobotSN);
            String remoteUrl = HTTP_REQUEST + goorServerIp + REMOTE_PATH_UPLOAD_URL;
            HttpClientUtil.executeMultipartFileUpload(null, url, remoteUrl , entry, null, true);
            return AjaxResult.success();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.failed("未获取相应的文件");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.failed("IO流处理异常");
        }

    }
}
