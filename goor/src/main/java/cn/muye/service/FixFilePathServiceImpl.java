package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.utils.HttpClientUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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
    public static final String FILENAME = "pathes.txt";
    //public static final String PREFIX_URL  = "D:\\path";

    @Value("${server.mapPath}")
    private String prefixUrl;

    @Value("${local.robot.SN}")
    private String localRobotSN;

    @Value("${goor.server.ip}")
    private String goorServerIp;

    @Override
    public AjaxResult handleFixFilePath(String sceneName) {
        logger.info("请求的场景名为" + sceneName);
        String url = prefixUrl + File.separator + sceneName + File.separator + FILENAME;
        logger.info("文件路径为" + url);
        Map entry = new HashMap();
        entry.put("sendId", localRobotSN);
        String remoteUrl = HTTP_REQUEST + goorServerIp + REMOTE_PATH_UPLOAD_URL;
        logger.info("请求云端的url 为" + remoteUrl);
        //新建一个线程去传输
        new Thread(){
            @Override
            public void run() {
                try {
                    HttpClientUtil.executeMultipartFileUpload(null, url, remoteUrl, entry, null, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        logger.info("上传进行中。。。。");
        return AjaxResult.success();
    }

}
