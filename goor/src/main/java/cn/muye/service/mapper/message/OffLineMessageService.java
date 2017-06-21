package cn.muye.service.mapper.message;

import cn.mrobot.utils.StringUtil;
import cn.muye.mapper.config.AppConfigMapper;
import cn.muye.mapper.message.OffLineMessageMapper;
import cn.muye.model.config.AppConfig;
import cn.muye.model.message.OffLineMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */

@Service
@Transactional
public class OffLineMessageService {

    @Autowired
    private OffLineMessageMapper offLineMessageMapper;

    @Autowired
    private AppConfigMapper appConfigMapper;

    public OffLineMessage get(Long id){
        return offLineMessageMapper.get(id);
    }

    public OffLineMessage getByIsSuccess(boolean isSuccess){
        return offLineMessageMapper.getByIsSuccess(isSuccess);
    }

    public long save(OffLineMessage message) throws Exception{
        AppConfig config = appConfigMapper.get(1);
        message.setSenderId(config.getMpushUserId());
        message.setSendTime(new Date());
        return offLineMessageMapper.save(message);
    }

    public void update(OffLineMessage message) throws Exception{
        AppConfig config = appConfigMapper.get(1);
        message.setSenderId(config.getMpushUserId());
        message.setUpdateTime(new Date());
        offLineMessageMapper.update(message);
    }

    public List<OffLineMessage> list()  throws Exception{
        return offLineMessageMapper.list();
    }

    public List<OffLineMessage> listByIsSuccess(boolean isSuccess)  throws Exception{
        return offLineMessageMapper.listByIsSuccess(isSuccess);
    }

    public void delete(OffLineMessage message) throws Exception {
        offLineMessageMapper.delete(message);
    }

    public void deleteBySendTime(OffLineMessage message) throws Exception{
        offLineMessageMapper.deleteBySendTime(message);
    }

}
