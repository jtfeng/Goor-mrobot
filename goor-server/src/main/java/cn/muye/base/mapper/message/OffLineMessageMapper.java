package cn.muye.base.mapper.message;

import cn.muye.base.model.message.OffLineMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */
@Component
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
