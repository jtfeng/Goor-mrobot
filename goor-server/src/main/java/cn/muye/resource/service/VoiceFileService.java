package cn.muye.resource.service;

import cn.mrobot.bean.resource.VoiceFile;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by admin on 2017/7/28.
 */
public interface VoiceFileService extends BaseService<VoiceFile> {

    List<VoiceFile> listVoiceFiles(int page, int pageSize);

    VoiceFile getByVoiceKey(String voiceKey);
}
