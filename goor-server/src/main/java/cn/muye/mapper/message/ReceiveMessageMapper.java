package cn.muye.mapper.message;

import cn.muye.model.message.ReceiveMessage;

import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */
public interface ReceiveMessageMapper {

    ReceiveMessage get(Long id);

    ReceiveMessage getByIsSuccess(boolean isSuccess);

    long save(ReceiveMessage receiveMessage);

    void update(ReceiveMessage receiveMessage);

    List<ReceiveMessage> listByMessageStatus(ReceiveMessage receiveMessage);//两个list不要合并，因为boolean默认为false

    List<ReceiveMessage> listByIsSuccess(ReceiveMessage message);//两个list不要合并，因为boolean默认为false

    List<ReceiveMessage> listByMessageSuccess(ReceiveMessage message);

    List<ReceiveMessage> listByIdAndSenderId(ReceiveMessage receiveMessage);

    void delete(ReceiveMessage receiveMessage);

    void deleteBySendTime(ReceiveMessage receiveMessage);
}
