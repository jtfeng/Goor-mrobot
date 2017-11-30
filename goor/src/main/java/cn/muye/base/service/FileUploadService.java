package cn.muye.base.service;

/**
 * Created by Jelynn on 2017/11/28.
 */
public interface FileUploadService {

    /**
     * 打包上传地图文件
     * 2017 11 28删除，Artemis地图上传需要选择场景，使用方法uploadMapFile(String uuid, String sceneNames) 上传
     */
    @Deprecated
    void uploadMapFile(String uuid);

    /**
     * 打包上传地图文件
     */
    void uploadMapFile(String uuid, String sceneNames);

    void sendTopic(String errorCode, String uuid, String message);
}
