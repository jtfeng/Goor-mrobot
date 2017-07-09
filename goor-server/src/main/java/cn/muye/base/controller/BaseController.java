package cn.muye.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Selim on 2017/6/26.
 * 基类 controller
 */
public abstract class BaseController {
    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 注入下载推送值
     */
    @Value("${goor.push.dirs}")
    protected String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    protected String DOWNLOAD_HTTP;

}
