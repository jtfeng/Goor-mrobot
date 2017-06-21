package cn.muye.mapper.message;

import cn.muye.model.message.OffLineMessage;

import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */
public interface OffLineMessageMapper {

    OffLineMessage get(Long id);

    OffLineMessage getByIsSuccess(boolean isSuccess);

    long save(OffLineMessage offLineMessage);

    void update(OffLineMessage offLineMessage);

    List<OffLineMessage> list();

    List<OffLineMessage> listByIsSuccess(boolean isSuccess);

    void delete(OffLineMessage offLineMessage);

    void deleteBySendTime(OffLineMessage offLineMessage);
}
