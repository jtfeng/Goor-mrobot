package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.MapZip;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/11/27
 */
public interface MapAnalysisService {

    /**
     * 解析上传的地图
     * @param info
     * @throws Exception
     */
    void analysis(JSONObject info) throws Exception;

    /**
     * 解压地图压缩包，返回解压后的文件夹
     *
     * @param mapZip
     * @return
     */
    File unzipMapZipFile(MapZip mapZip);

    /**
     * 解析解压后的文件
     *
     * @param mapFilePath
     * @param mapZip
     * @throws Exception
     */
    void analysisFile(File mapFilePath, List<String> sceneNames, MapZip mapZip) throws Exception;
}
