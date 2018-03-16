package cn.muye.order.controller;

import cn.mrobot.utils.PoiExcelExportUtil;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.bean.export.DestinationAnalysisVO;
import cn.muye.order.bean.export.ElevatorUseAnalysisVO;
import cn.muye.order.bean.export.OrderExportData;
import cn.muye.order.bean.export.TransferTaskAnalysisVO;
import cn.muye.order.service.OrderExportDataService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2018/3/13.
 */
@Controller
public class OrderExportDataController extends BaseController{

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    @Autowired
    private OrderExportDataService orderExportDataService;

    private static final String ORDER_EXPORT_FILE_NAME = "诺亚后台数据记录.xls";

    @RequestMapping(value = "logInfo/export/orderData", method = RequestMethod.GET)
    public void exportOrderData(@RequestParam(name = "stationId", required = false)Long stationId,
                                      @RequestParam(name = "startDate", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date startDate,
                                      @RequestParam(name = "endDate", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date endDate,
                                      HttpServletResponse response) {
        //查询出符合条件的数据
        OrderExportData orderExportData = orderExportDataService.getOrderExportData(stationId, startDate, endDate);
        List<TransferTaskAnalysisVO> transferTaskAnalysisVOList = orderExportData.getTransferTaskAnalysisVOList();
        List<ElevatorUseAnalysisVO> elevatorUseAnalysisVOList = orderExportData.getElevatorUseAnalysisVOList();
        List<DestinationAnalysisVO> destinationAnalysisVOList = orderExportData.getDestinationAnalysisVOList();
        try {
            List<PoiExcelExportUtil.SheetMain> sheetMainList = Lists.newArrayList();
            //运送任务分析
            PoiExcelExportUtil.SheetMain<TransferTaskAnalysisVO> sheetMain = new PoiExcelExportUtil.SheetMain<>();
            sheetMain.init(0, 0, 0, Arrays.asList(8, 15, 15, 10, 15, 20, 15, 15, 15, 15, 18, 15, 20, 20, 15, 15),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 16, true, false, true, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, true, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true));
            sheetMain.setSheetName("运送任务分析");
            sheetMain.setSheetTitle("运送任务分析");
            sheetMain.setSheetHeads(Arrays.asList("序号", "日期", "场景", "订单编号", "机器人编号", "运送物资", "目的地", "目的地数量", "下单时间", "执行订单时间", "到达发车点时间", "发车时间", "返回时间（待命点）", "机器人运输总时间", "发车装货时间", "本单总时间"));
            sheetMain.setTs(transferTaskAnalysisVOList);
            sheetMainList.add(sheetMain);
            //电梯运送分析
            PoiExcelExportUtil.SheetMain<ElevatorUseAnalysisVO> sheetMain2 = new PoiExcelExportUtil.SheetMain<>();
            sheetMain2.init(0, 0, 1, Arrays.asList(8, 15, 15, 10, 15, 25, 30, 30, 20, 25, 20),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 16, true, false, true, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, true, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true));
            sheetMain2.setSheetName("电梯使用分析");
            sheetMain2.setSheetTitle("电梯使用分析");
            sheetMain2.setSheetHeads(Arrays.asList("序号", "日期", "场景", "订单编号", "机器人编号", "到达抢电梯锁点时间", "机器人进电梯启动行走时间", "从电梯出来到达解锁点的时间", "等待电梯时间", "机器人占用电梯时间", "电梯阶段总时间"));
            sheetMain2.setTs(elevatorUseAnalysisVOList);
            sheetMainList.add(sheetMain2);
            //目的地人机交互分析
            PoiExcelExportUtil.SheetMain<DestinationAnalysisVO> sheetMain3 = new PoiExcelExportUtil.SheetMain<>();
            sheetMain3.init(0, 0, 2, Arrays.asList(8, 15, 15, 10, 15, 15, 20, 20, 25),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 16, true, false, true, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, true, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true),
                    PoiExcelExportUtil.SheetMain.createCellStyle((short) 11, false, false, false, true));
            sheetMain3.setSheetName("目的地人机交互分析");
            sheetMain3.setSheetTitle("目的地人机交互分析");
            sheetMain3.setSheetHeads(Arrays.asList("序号", "日期", "场景", "订单编号", "机器人编号", "目的地", "到达目的地时间", "刷卡返回时间", "目的地人机交互总时间"));
            sheetMain3.setTs(destinationAnalysisVOList);
            sheetMainList.add(sheetMain3);
            //存储路径
            /*String fileOppositePath = SearchConstants.FAKE_MERCHANT_STORE_ID + File.separator + Constant.EXPORT_DIR_NAME + File.separator + Constant.ORDER_DATA;
            File fileDir = new File(DOWNLOAD_HOME + File.separator + fileOppositePath);
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }*/
            // 下载文件的默认名称
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(ORDER_EXPORT_FILE_NAME, "utf-8"));
            //outputStream = new FileOutputStream(DOWNLOAD_HOME + File.separator + fileOppositePath + File.separator + ORDER_EXPORT_FILE_NAME);
            PoiExcelExportUtil.createExcelFile(response.getOutputStream(), sheetMainList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {

        }
    }
}
