package cn.muye.resource.service.impl;

import cn.mrobot.bean.resource.Resource;
import cn.mrobot.bean.resource.VoiceFile;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.resource.service.VoiceFileService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by admin on 2017/7/28.
 */
@Service
@Transactional
public class VoiceFileServiceImpl extends BaseServiceImpl<VoiceFile> implements VoiceFileService {

    @Override
    public List<VoiceFile> listVoiceFiles(int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        Example example = new Example(Resource.class);
        example.setOrderByClause("CREATE_TIME DESC");
        return myMapper.selectByExample(example);
    }

    @Override
    public VoiceFile getByVoiceKey(String voiceKey) {
        Example example = new Example(VoiceFile.class);
        example.createCriteria().andCondition("VOICE_KEY =", voiceKey);
        List<VoiceFile> list = myMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
