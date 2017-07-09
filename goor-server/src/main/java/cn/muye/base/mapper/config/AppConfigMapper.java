package cn.muye.base.mapper.config;

import cn.muye.base.model.config.AppConfig;
import org.springframework.stereotype.Component;

/**
 * Created by enva on 2017/5/11.
 */
@Component
public interface AppConfigMapper {

    AppConfig get(Integer id);

}
