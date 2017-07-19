package cn.muye.base.service.mapper.message;

import cn.muye.base.mapper.message.OffLineMessageMapper;
import cn.muye.base.model.message.OffLineMessage;
import com.github.pagehelper.PageHelper;
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

    public OffLineMessage get(Long id){
        if(null == id){
            return null;
        }
        return offLineMessageMapper.get(id);
    }

    public OffLineMessage getByIsSuccess(boolean isSuccess){
        return offLineMessageMapper.getByIsSuccess(isSuccess);
    }

    public long save(OffLineMessage message) throws Exception {
        if(null == message){
            return 0L;
        }
        message.setSendTime(new Date());
        return offLineMessageMapper.save(message);
    }

    public void update(OffLineMessage message) throws Exception {
        if(null == message){
            return;
        }
        message.setUpdateTime(new Date());
        offLineMessageMapper.update(message);
    }

    public List<OffLineMessage> pageList(int page, int pageSize) throws Exception {
        PageHelper.startPage(page, pageSize);
        return offLineMessageMapper.pageList();
    }

    public List<OffLineMessage> listByIsSuccess(boolean isSuccess) throws Exception {
        return offLineMessageMapper.listByIsSuccess(isSuccess);
    }

    public void delete(OffLineMessage message) throws Exception {
        offLineMessageMapper.delete(message);
    }

    public void deleteBySendTime(OffLineMessage message) throws Exception{
        offLineMessageMapper.deleteBySendTime(message);
    }

}
