package cn.muye.base.export.service.impl;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.export.service.ExportService;
import cn.muye.log.base.service.LogInfoService;
import cn.muye.log.charge.service.ChargeInfoService;
import cn.muye.log.mission.service.LogMissionService;
import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Created by Jelynn on 2017/10/30.
 *
 * @author Jelynn
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final String Underline = "_";
    private static Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);
    //CSV文件分隔符
    private static final String NEW_LINE_SEPARATOR = "\n";
    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;

    @Autowired
    private LogInfoService logInfoService;

    @Autowired
    private LogMissionService logMissionService;

    @Autowired
    private ChargeInfoService chargeInfoService;

    @Override
    public void exportLogToFile() {
        logger.info("开始定时任务，导出日志数据到CSV文件");
        exportToCSV();
    }

    private void exportToCSV() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            try {
                exportLogInfoToFile();
            } catch (Exception e) {
                logger.info("导出LogInfo数据到CSV文件出错");
            }
        });

        executorService.execute(() -> {
            try {
                exportLogMissionToFile();
            } catch (Exception e) {
                logger.info("导出LogMission数据到CSV文件出错");
            }
        });

        executorService.execute(() -> {
            try {
                exportChargeInfoToFile();
            } catch (Exception e) {
                logger.info("导出ChargeInfo数据到CSV文件出错");
            }
        });
    }

    private void exportLogInfoToFile() throws Exception {
        logger.info("导出LogChargeInfo数据到CSV文件");
        List<LogInfo> logInfoList = logInfoService.lists(null, SearchConstants.FAKE_MERCHANT_STORE_ID);
        LogInfo.class.getDeclaredFields();
        String[] headers = {"数据库ID", "设备编号", "日志等级", "日志类型", "模块", "场景名",
                "地图名", "具体信息", "警告错误处理人", "警告错误处理时间", "创建时间"};
        String[] fields = {"deviceId", "logLevel", "logType", "module", "sceneName",
                "mapName", "message", "handlePerson", "handleTime"};
        String filePath = writeToCSV(LogInfo.class, logInfoList, headers, fields);
        if (StringUtil.isNotBlank(filePath)) {
            logger.info("成功导出LogChargeInfo数据到CSV文件，文件路径=" + filePath);
            logInfoService.delete(logInfoList);
        }
    }

    private void exportLogMissionToFile() throws Exception {
        logger.info("导出LogMission数据到CSV文件");
        List<LogMission> logMissionList = logMissionService.listAll();
        String[] headers = {"数据库ID", "设备编号", "任务日志类型", "任务列表ID", "任务ID", "任务节点ID", "任务item的name",
                "任务列表重复", "任务重复", "mission事件", "事件描述", "充电状态", "是否插入充电桩", "电量", "ros当前位置信息", "创建时间"};
        String[] fields = {"robotCode", "missionType", "missionListId", "missionId", "missionItemId", "missionItemName",
                "missionListRepeatTimes", "missionRepeatTimes", "missionEvent", "missionDescription", "chargingStatus", "pluginStatus", "powerPercent", "ros"};
        String filePath = writeToCSV(LogMission.class, logMissionList, headers, fields);
        if (StringUtil.isNotBlank(filePath)) {
            logger.info("成功导出LogMission数据到CSV文件，文件路径=" + filePath);
            logMissionService.delete(logMissionList);
        }
    }

    private void exportChargeInfoToFile() throws Exception {
        logger.info("导出LogInfo数据到CSV文件");
        List<ChargeInfo> chargeInfoList = chargeInfoService.listAll();
        String[] headers = {"数据库ID", "设备编号", "充电状态", "是否插入充电桩", "电量", "是否打开自动回充", "创建时间"};
        String[] fields = {"deviceId", "chargingStatus", "pluginStatus", "powerPercent", "autoCharging"};
        String filePath = writeToCSV(ChargeInfo.class, chargeInfoList, headers, fields);
        if (StringUtil.isNotBlank(filePath)) {
            logger.info("成功导出LogInfo数据到CSV文件，文件路径=" + filePath);
            chargeInfoService.delete(chargeInfoList);
        }
    }

    /**
     * headers 为CSV文件的文件头， fields 为对象的属性名，
     * 除创建时间外，headers与fields的字段需一一对应
     * headers 始终比fields多一个元素，最后的创建时间
     *
     * @param dataList
     * @param headers
     * @param fields
     * @return 返回导出文件路径
     */
    private <E extends BaseBean> String writeToCSV(Class<E> clazz, List<E> dataList, String[] headers, String[] fields) {
        if (dataList == null || dataList.size() == 0 || headers.length == 0 || fields.length == 0) {
            return "";
        }
        //以属性名长度为准
        int length = fields.length;
        //获取对象名称，
        String name = clazz.getName();
        String className = name.substring(name.lastIndexOf(".") + 1);
        File lgFile = createExportfile(className);

        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR).withFirstRecordAsHeader();
        // 这是写入CSV的代码
        OutputStreamWriter out = null;
        CSVPrinter printer = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(lgFile), Constant.CSV_CHARSET_CHINESE);
            printer = new CSVPrinter(out, format);
            //写入列头数据
            printer.printRecord(headers);
            for (E bean : dataList) {
                List<String> logInfoRecord = Lists.newArrayList();
                logInfoRecord.add(bean.getId().toString());
                for (int i = 0; i < length; i++) {
                    String fieldName = fields[i];
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(bean);
                    logInfoRecord.add(value != null ? value.toString() : "");
                }
                logInfoRecord.add(DateTimeUtils.getDefaultDateString(bean.getCreateTime()));
                printer.printRecord(logInfoRecord);
            }
        } catch (Exception e) {
            logger.error("导出" + className + "日志文件出错", e);
            return "";
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != printer) {
                    printer.close();
                }
            } catch (IOException e) {
                logger.error("导出" + className + "日志文件出错", e);
            }
            return lgFile.getAbsolutePath();
        }
    }

    private File createExportfile(String name) {
        //创建导出文件
        String fileOppositePath = SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator
                + Constant.EXPORT_DIR_NAME + File.separator + name + File.separator + DateTimeUtils.getMonth();
        File fileDir = new File(DOWNLOAD_HOME + File.separator + fileOppositePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String logFileName = name + Underline + DateTimeUtils.getNormalNameDateTime() + Constant.LOG_FILE_SUFFIX;
        return new File(fileDir, logFileName);
    }
}