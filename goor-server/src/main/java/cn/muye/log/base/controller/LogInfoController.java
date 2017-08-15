package cn.muye.log.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.log.base.service.LogInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 16:38
 * Describe:
 * Version:1.0
 */
@Controller
public class LogInfoController {

    private static Logger LOGGER = LoggerFactory.getLogger(LogInfoController.class);

    @Autowired
    private LogInfoService logInfoService;

    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Value("${goor.push.http}")
    private String DOWNLOAD_HTTP;

    private static final String LOG_FILE_PREFIX = "日志导出_";
    private static final String LOG_FILE_SUFFIX = ".csv";
    //CSV文件分隔符
    private static final String NEW_LINE_SEPARATOR = "\n";

    @RequestMapping(value = "loginfo/list", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listLogInfo(HttpServletRequest request, WhereRequest whereRequest) {
        try {
            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();

            pageNo = pageNo == null ? 1 : pageNo;
            pageSize = pageSize == null ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            //用PageInfo对结果进行包装
            List<LogInfo> logInfoList = logInfoService.lists(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<LogInfo> page = new PageInfo<LogInfo>(logInfoList);
            return AjaxResult.success(page);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1, "日志查询错误");
        }
    }

    @RequestMapping(value = "loginfo/export", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult exportLogInfo(HttpServletRequest request, WhereRequest whereRequest) {
        OutputStreamWriter out = null;
        CSVPrinter printer = null;
        try {
            //查询出符合条件的数据
            List<LogInfo> logInfoList = logInfoService.lists(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            //导出到csv文件中
            //CSV文件相对路径
            String fileOppositePath = SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + Constant.EXPORT_DIR_NAME + File.separator + Constant.LOG_DIR_NAME;
            File fileDir = new File(DOWNLOAD_HOME + File.separator + fileOppositePath);
            if (!fileDir.exists())
                fileDir.mkdirs();
            String logFileName = LOG_FILE_PREFIX + DateTimeUtils.getNormalNameDateTime() + LOG_FILE_SUFFIX;
            File lgFile = new File(fileDir, logFileName);

            // 创建CSV写对象
            String[] FILE_HEADER = new String[]{"门店ID", "设备编号", "日志等级", "日志类型", "模块", "场景名", "地图名", "具体信息", "创建时间", "警告错误处理人", "警告错误处理时间"};
            CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR).withFirstRecordAsHeader();

            // 这是写入CSV的代码
            out = new OutputStreamWriter(new FileOutputStream(lgFile), "GB2312");
            printer = new CSVPrinter(out, format);
            //写入列头数据
            printer.printRecord(FILE_HEADER);
            for (LogInfo logInfo : logInfoList) {
                List<String> logInfoRecord = Lists.newArrayList();
                logInfoRecord.add(String.valueOf(logInfo.getStoreId()));
                logInfoRecord.add(logInfo.getDeviceId());
                LogLevel logLevel = LogLevel.getLogLevel(logInfo.getLogLevel());
                logInfoRecord.add(logLevel == null ? "" : logLevel.getValue());
                LogType logType = LogType.getLogType(logInfo.getLogType());
                logInfoRecord.add(logType == null ? "" : logType.getValue());
                ModuleEnums moduleEnums = ModuleEnums.getModuleEnums(logInfo.getModule());
                logInfoRecord.add(moduleEnums == null ? "" : moduleEnums.getModuleName());
                logInfoRecord.add(logInfo.getSceneName());
                logInfoRecord.add(logInfo.getMapName());
                logInfoRecord.add(logInfo.getMessage());
                logInfoRecord.add(DateTimeUtils.getDefaultDateString(logInfo.getCreateTime()));
                logInfoRecord.add(logInfo.getHandlePerson());
                logInfoRecord.add(DateTimeUtils.getDefaultDateString(logInfo.getHandleTime()));
                printer.printRecord(logInfoRecord);
            }

            String logFileHttpPath = DOWNLOAD_HTTP + File.separator + fileOppositePath + File.separator + logFileName;
            logFileHttpPath = logFileHttpPath.replaceAll("\\\\", "/");
            return AjaxResult.success(logFileHttpPath, "导出成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1, "日志导出错误");
        } finally {
            try {
                if (null != printer)
                    printer.flush();
                printer.close();
                if (null != out)
                    out.close();
            } catch (Exception e) {
                LOGGER.error("日志导出错误", e);
            }
        }
    }
}
