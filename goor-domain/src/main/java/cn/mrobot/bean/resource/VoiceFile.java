package cn.mrobot.bean.resource;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by admin on 2017/7/28.
 */
@Table(name = "VOICE_FILE")
public class VoiceFile extends BaseBean {

    private String voiceKey;

    private String fileName;

    public String getVoiceKey() {
        return voiceKey;
    }

    public void setVoiceKey(String voiceKey) {
        this.voiceKey = voiceKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
