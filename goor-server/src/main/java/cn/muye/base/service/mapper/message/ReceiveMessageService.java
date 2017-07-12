package cn.muye.base.service.mapper.message;

import cn.muye.base.mapper.config.AppConfigMapper;
import cn.muye.base.mapper.message.ReceiveMessageMapper;
import cn.muye.base.model.config.AppConfig;
import cn.muye.base.model.message.ReceiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */

@Service
@Transactional
public class ReceiveMessageService {

    @Autowired
    private ReceiveMessageMapper receiveMessageMapper;

    @Autowired
    private AppConfigMapper appConfigMapper;

    public ReceiveMessage get(Long id){
        return receiveMessageMapper.get(id);
    }

    public ReceiveMessage getByIsSuccess(boolean isSuccess){
        return receiveMessageMapper.getByIsSuccess(isSuccess);
    }

    public long save(ReceiveMessage message)  throws Exception{
        if(null != message && StringUtils.isEmpty(message.getUuId())){
            return 0L;
        }
        AppConfig config = appConfigMapper.get(1);
        if(!config.getMpushUserId().equals(message.getReceiverId())){
            return 0L;
        }
        message.setUpdateTime(new Date());
        message.setSuccess(false);//设置为false
        return receiveMessageMapper.save(message);
    }

    public void update(ReceiveMessage message)  throws Exception{
        message.setUpdateTime(new Date());
        receiveMessageMapper.update(message);
    }

    public List<ReceiveMessage> listByMessageStatus(ReceiveMessage message)  throws Exception{
        return receiveMessageMapper.listByMessageStatus(message);
    }

    public List<ReceiveMessage> listByIsSuccess(ReceiveMessage message) throws Exception {
        return receiveMessageMapper.listByIsSuccess(message);
    }

    public List<ReceiveMessage> listByMessageSuccess(ReceiveMessage message) throws Exception{
        return receiveMessageMapper.listByMessageSuccess(message);
    }

    public List<ReceiveMessage> listByUUID(ReceiveMessage message) throws Exception {
        return receiveMessageMapper.listByUUID(message);
    }

    public void delete(ReceiveMessage message) throws Exception {
        receiveMessageMapper.delete(message);
    }

    public void deleteBySendTime(ReceiveMessage message) throws Exception{
        receiveMessageMapper.deleteBySendTime(message);
    }

}
