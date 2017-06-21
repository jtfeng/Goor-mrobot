package cn.muye.model.config;

import cn.muye.bean.MessageInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by enva on 2017/5/9.
 */
public class AppConfig implements Serializable {

    private Long id;

    private String mpushPublicKey;

    private String mpushAllocServer;

    private String mpushPushServer;

    private String mpushDeviceId;

    private String mpushOsName;

    private String mpushOsVersion;

    private String mpushClientVersion;

    private String mpushUserId;

    private String mpushTags;

    private String mpushSessionStorageDir;

    private String rosPath;

    public AppConfig(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMpushPublicKey() {
        return mpushPublicKey;
    }

    public void setMpushPublicKey(String mpushPublicKey) {
        this.mpushPublicKey = mpushPublicKey;
    }

    public String getMpushAllocServer() {
        return mpushAllocServer;
    }

    public void setMpushAllocServer(String mpushAllocServer) {
        this.mpushAllocServer = mpushAllocServer;
    }

    public String getMpushPushServer() {
        return mpushPushServer;
    }

    public void setMpushPushServer(String mpushPushServer) {
        this.mpushPushServer = mpushPushServer;
    }

    public String getMpushDeviceId() {
        return mpushDeviceId;
    }

    public void setMpushDeviceId(String mpushDeviceId) {
        this.mpushDeviceId = mpushDeviceId;
    }

    public String getMpushOsName() {
        return mpushOsName;
    }

    public void setMpushOsName(String mpushOsName) {
        this.mpushOsName = mpushOsName;
    }

    public String getMpushOsVersion() {
        return mpushOsVersion;
    }

    public void setMpushOsVersion(String mpushOsVersion) {
        this.mpushOsVersion = mpushOsVersion;
    }

    public String getMpushClientVersion() {
        return mpushClientVersion;
    }

    public void setMpushClientVersion(String mpushClientVersion) {
        this.mpushClientVersion = mpushClientVersion;
    }

    public String getMpushUserId() {
        return mpushUserId;
    }

    public void setMpushUserId(String mpushUserId) {
        this.mpushUserId = mpushUserId;
    }

    public String getMpushTags() {
        return mpushTags;
    }

    public void setMpushTags(String mpushTags) {
        this.mpushTags = mpushTags;
    }

    public String getMpushSessionStorageDir() {
        return mpushSessionStorageDir;
    }

    public void setMpushSessionStorageDir(String mpushSessionStorageDir) {
        this.mpushSessionStorageDir = mpushSessionStorageDir;
    }

    public String getRosPath() {
        return rosPath;
    }

    public void setRosPath(String rosPath) {
        this.rosPath = rosPath;
    }
}
