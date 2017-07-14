package cn.muye.base.service;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.ZipUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by Jelynn on 2017/7/10.
 */
@Service
public class MapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);

    @Value("${local.robot.SN}")
    private String deviceId;

    public void downLoadAndUnzipMap(String topicData) {
        if (StringUtil.isNullOrEmpty(topicData)) {
            LOGGER.info("地图压缩包数据为空");
            return;
        }
        MapZip mapZip = JSON.parseObject(topicData, MapZip.class);
        try {
            String savePath = mapZip.getRobotPath();
            String fileName = mapZip.getFileName();
            String downLoadFilePath = savePath + File.separator + fileName;
            boolean downLoadFlag = HttpClientUtil.executeDownloadFile(null, mapZip.getFileHttpPath(), downLoadFilePath, "UTF-8", true);
            if (downLoadFlag) {
                boolean unzipFlag = ZipUtils.unzip(downLoadFilePath, "E:\\unzip", false);
                if (unzipFlag) {
                    LOGGER.info("地图压缩包解压成功，savePath = " + savePath);
                }
            }
        } catch (Exception e) {
            LOGGER.error("下载地图压缩包出错，remoteURL= " + mapZip.getFileHttpPath());
        }

    }
}
