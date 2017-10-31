package cn.muye.base.export.service;

import cn.muye.base.export.service.impl.ExportServiceImpl;

/**
 * Created by Jelynn on 2017/10/30.
 * @author Jelynn
 */
public interface ExportService {

    /**
     * 导出日志表数据到文件，包括包括LOG_CHARGE_INFO，LOG_INFO，LOG_MISSION
     */
    void exportLogToFile();

}
