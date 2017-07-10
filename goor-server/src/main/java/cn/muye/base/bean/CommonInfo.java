package cn.muye.base.bean;

import java.io.Serializable;

/**
 * 通用文件下载bean
 * Created by enva on 2017/5/9.
 */
public class CommonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String MD5;//md5,有文件时，必须字段

    private String remoteFileUrl;//下载路径,有文件时，必须字段

    private String localPath;//本地路径,有文件时，必须字段

    private String localFileName;//本地文件名称,有文件时，必须字段

    private String topicName;//需要发布的topic名称，必须字段

    private String topicType;//需要发布的topic消息类型，必须字段

    private String publishMessage;//需要发布的内容，必须字段

    public CommonInfo(){

    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getRemoteFileUrl() {
        return remoteFileUrl;
    }

    public void setRemoteFileUrl(String remoteFileUrl) {
        this.remoteFileUrl = remoteFileUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getPublishMessage() {
        return publishMessage;
    }

    public void setPublishMessage(String publishMessage) {
        this.publishMessage = publishMessage;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }
}
