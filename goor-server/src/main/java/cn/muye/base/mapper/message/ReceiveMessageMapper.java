package cn.muye.base.mapper.message;

import cn.muye.base.model.message.ReceiveMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */
@Component
public interface ReceiveMessageMapper {

    ReceiveMessage get(Long id);

    ReceiveMessage getByIsSuccess(boolean isSuccess);

    long save(ReceiveMessage receiveMessage);

    void update(ReceiveMessage receiveMessage);

    List<ReceiveMessage> listByMessageStatus(ReceiveMessage receiveMessage);//两个list不要合并，因为boolean默认为false

    List<ReceiveMessage> listByIsSuccess(ReceiveMessage message);//两个list不要合并，因为boolean默认为false

    List<ReceiveMessage> listByMessageSuccess(ReceiveMessage message);

    List<ReceiveMessage> listByUUID(ReceiveMessage receiveMessage);

    void delete(ReceiveMessage receiveMessage);

    void deleteBySendTime(ReceiveMessage receiveMessage);
}
