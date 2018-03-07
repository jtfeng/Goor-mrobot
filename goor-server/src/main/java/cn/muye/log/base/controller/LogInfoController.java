package cn.muye.log.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.base.bean.RobotLogWarningDetail;
import cn.muye.log.base.bean.RobotLogWarningVO;
import cn.muye.log.base.service.LogInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private RobotService robotService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    private static final String LOG_FILE_PREFIX = "goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_RZDC_";
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
            return AjaxResult.failed(1, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_RZCXCW"));
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
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }
            String logFileName = localeMessageSourceService.getMessage(LOG_FILE_PREFIX) + DateTimeUtils.getNormalNameDateTime() + Constant.LOG_FILE_SUFFIX;
            File lgFile = new File(fileDir, logFileName);

            // 创建CSV写对象
            String[] FILE_HEADER ={localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_MDID"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_SBBH"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_RZDJ"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_RZLX"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_MK"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_CJM"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_DTM"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_JTXX"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_CJSJ"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_JGCWCLR"), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_JGCWCLSJ")};
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
            return AjaxResult.success(logFileHttpPath, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_DCCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_RZDCCW"));
        } finally {
            try {
                if (null != printer) {
                    printer.flush();
                    printer.close();
                }
                if (null != out){
                    out.close();
                }

            } catch (Exception e) {
                LOGGER.error("日志导出错误", e);
            }
        }
    }

    /**
     * PAD 端 显示机器人警报信息
     * @param robotId
     * @return
     */
    @RequestMapping(value = "logInfo/robotWarningLogs", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult robotWarningLogs(@RequestParam(value = "robotId",required = false)Long robotId){
        try {
            Robot queryRobot = robotService.getById(robotId);
            List<RobotLogWarningVO> robotLogWarningVOs = Lists.newArrayList();
            if(queryRobot!= null){
                for (int i = 0; i < 7; i++) {
                    //最近7天遍历
                    Date startToday = DateTimeUtils.startSomeDay(-i);
                    Date endToday = DateTimeUtils.endSomeDay(-i);
                    List<LogInfo> logInfoList = logInfoService.listWarningLogsByRobotAndTime(queryRobot.getCode(), startToday, endToday);
                    RobotLogWarningVO logWarningVO = generateRobotLogWarning(logInfoList, startToday);
                    robotLogWarningVOs.add(logWarningVO);
                }
            }
            Map<String, Object> returnMap = Maps.newLinkedHashMap();
            returnMap.put(queryRobot.getCode(), robotLogWarningVOs);
            return AjaxResult.success(returnMap, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_HQJQRJBXXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_base_controller_LogInfoController_java_XTNBCC"));
        }
    }

    private RobotLogWarningVO generateRobotLogWarning(List<LogInfo> logInfoList, Date logDate){
        RobotLogWarningVO robotLogWarningVO = new RobotLogWarningVO();
        List<RobotLogWarningDetail> robotLogWarningDetailList = logInfoList.stream().map(logInfo -> {
            RobotLogWarningDetail robotLogWarningDetail = new RobotLogWarningDetail();
            robotLogWarningDetail.setDateTime(logInfo.getCreateTime());
            robotLogWarningDetail.setType(LogType.getLogType(logInfo.getLogType()).getValue());
            return robotLogWarningDetail;
        }).collect(Collectors.toList());
        robotLogWarningVO.setWarningTime(logInfoList.size());
        robotLogWarningVO.setDate(logDate);
        robotLogWarningVO.setWarningDetails(robotLogWarningDetailList);
        return robotLogWarningVO;
    }
}
