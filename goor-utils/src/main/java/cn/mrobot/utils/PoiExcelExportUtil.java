package cn.mrobot.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * poi生成excel文件工具类
 *
 * @author selim
 */
public class PoiExcelExportUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoiExcelExportUtil.class);

    /**
     * 创建Excel文件
     *
     * @param outputStream
     * @param sheetMainList
     * @return
     * @throws Exception
     */
    public static void createExcelFile(OutputStream outputStream, List<SheetMain> sheetMainList) throws Exception {

        //1.创建工作簿
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        for (SheetMain sheetMain : sheetMainList) {
            int headWidth = 0;
            int contentHeight = 0;
            int currentRow = 0;
            //--数据检查
            if (null == sheetMain) {
                throw new RuntimeException("创建EXCEL文件-主对象为空");
            }
            if (null == sheetMain.getSheetTitle()) {
                LOGGER.warn("创建EXCEL文件-主标题为空");
            }
            if (null == sheetMain.getSheetHeads() || sheetMain.getSheetHeads().size() == 0) {
                LOGGER.warn("创建EXCEL文件-列标题为空");
            } else {
                headWidth = sheetMain.getSheetHeads().size();
            }
            if (null == sheetMain.getTs() || sheetMain.getTs().size() == 0) {
                LOGGER.warn("创建EXCEL文件-列内容为空");
            } else {
                contentHeight = sheetMain.getTs().size();
            }
            //1.1创建合并单元格对象
            CellRangeAddress callRangeAddress = null;
            if (null != sheetMain.getSheetTitle()) {
                callRangeAddress = new CellRangeAddress(sheetMain.getStartRow(), sheetMain.getStartRow(), sheetMain.getStartCell(), (headWidth + sheetMain.getStartCell()) - (headWidth == 0 ? 0 : 1));//起始行,结束行,起始列,结束列
            }
            //1.2头标题样式
            HSSFCellStyle headStyle = createHSSFCellStyle(hssfWorkbook, "宋体", sheetMain.getTitleCellStyle().getFontSize(), sheetMain.getTitleCellStyle().isFontBold(), sheetMain.getTitleCellStyle().isBorderFlag(), sheetMain.getTitleCellStyle().isAlignmentCenter(), sheetMain.getTitleCellStyle().isVerticalAlignmentCenter());
            //1.3列标题样式
            HSSFCellStyle colHeadStyle = createHSSFCellStyle(hssfWorkbook, "宋体", sheetMain.getHeadCellStyle().getFontSize(), sheetMain.getHeadCellStyle().isFontBold(), sheetMain.getHeadCellStyle().isBorderFlag(), sheetMain.getHeadCellStyle().isAlignmentCenter(), sheetMain.getHeadCellStyle().isVerticalAlignmentCenter());
            //1.4列样式
            HSSFCellStyle colStyle = createHSSFCellStyle(hssfWorkbook, "宋体", sheetMain.gettCellStyle().getFontSize(), sheetMain.gettCellStyle().isFontBold(), sheetMain.gettCellStyle().isBorderFlag(), sheetMain.gettCellStyle().isAlignmentCenter(), sheetMain.gettCellStyle().isVerticalAlignmentCenter());
            //1.4列样式
            HSSFCellStyle colFooterStyle = createHSSFCellStyle(hssfWorkbook, "宋体", sheetMain.getFooterCellStyle().getFontSize(), sheetMain.getFooterCellStyle().isFontBold(), sheetMain.getFooterCellStyle().isBorderFlag(), sheetMain.getFooterCellStyle().isAlignmentCenter(), sheetMain.getFooterCellStyle().isVerticalAlignmentCenter());
            //2.创建工作表
            HSSFSheet sheet = hssfWorkbook.createSheet();
            hssfWorkbook.setSheetName(sheetMain.getSheetNum(), sheetMain.getSheetName());
            //2.1加载合并单元格对象
            if (headWidth != 0 && null != sheetMain.getSheetTitle()) {
                sheet.addMergedRegion(callRangeAddress);
            }
            //设置默认列宽
            if (null != sheetMain.getCellWidths() && sheetMain.getCellWidths().size() != 0) {
                for (int i = 0; i < sheetMain.getCellWidths().size(); i++) {
                    sheet.setColumnWidth(i, ((int) sheetMain.getCellWidths().get(i)) * 256);
                }
            } else {
                sheet.setDefaultColumnWidth(15);
            }
            //3.创建行
            //3.1创建头标题行;并且设置头标题
            HSSFRow row = sheet.createRow(sheetMain.getStartRow());
            HSSFCell cell = row.createCell(sheetMain.getStartCell());
            //加载单元格样式
            cell.setCellStyle(headStyle);
            if (null != sheetMain.getSheetTitle()) {
                cell.setCellValue(sheetMain.getSheetTitle());
            }
            if (sheetMain.getTitleCellStyle().isBorderFlag()) {
                RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress, sheet); // 下边框
                RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress, sheet); // 左边框
                RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress, sheet); // 有边框
                RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress, sheet); // 上边框
            }

            if (null != sheetMain.getSheetTitle()) {
                currentRow = currentRow + 1;
            }
            //3.2创建列标题;并且设置列标题
            if (null != sheetMain.getSheetHeads() && sheetMain.getSheetHeads().size() != 0) {
                HSSFRow row2 = sheet.createRow(currentRow);
                String[] titles = (String[]) sheetMain.getSheetHeads().toArray();
                for (int i = 0; i < titles.length; i++) {
                    HSSFCell cell2 = row2.createCell(sheetMain.getStartCell() + i);
                    //加载单元格样式
                    cell2.setCellStyle(colHeadStyle);
                    cell2.setCellValue(titles[i]);
                }
            }
            //4创建列表内容
            if (null != sheetMain.getSheetHeads() && sheetMain.getSheetHeads().size() != 0) {
                currentRow = currentRow + 1;
            }
            List ts = sheetMain.getTs();
            if (ts != null) {
                for (int j = 0; j < ts.size(); j++) {
                    HSSFRow row3 = sheet.createRow(sheetMain.getStartRow() + currentRow);
                    // 得到类对象
                    Class shellContentCla = (Class) ts.get(j).getClass();
                /* 得到类中的所有属性集合 */
                    Field[] shellContentField = shellContentCla.getDeclaredFields();
                    for (int i = 0; i < shellContentField.length; i++) {
                        Field field = shellContentField[i];
                        field.setAccessible(true);
                        Object val = field.get(ts.get(j));
                        //创建数据行,前面有两行,头标题行和列标题行
                        HSSFCell cell1 = row3.createCell(sheetMain.getStartCell() + i);
                        cell1.setCellStyle(colStyle);
                        cell1.setCellValue("");
                        if (null != val) {
                            cell1.setCellValue(val.toString());
                        }
                    }
                    currentRow = currentRow + 1;
                }
            }
            //5创建脚标内容
            List<SheetFooter> sheetFooters = sheetMain.getSheetFooters();
            if(sheetFooters != null){
                currentRow = currentRow + 1;
                HSSFRow row4 = sheet.createRow(currentRow);
                for (int i = 0; i < sheetFooters.size(); i++) {
                    SheetFooter sheetFooter = sheetFooters.get(i);
                    HSSFCell cell1 = row4.createCell(sheetMain.getStartCell() + 2 * i);
                    cell1.setCellStyle(colHeadStyle);
                    cell1.setCellValue(sheetFooter.getName());

                    HSSFCell cell2 = row4.createCell(sheetMain.getStartCell() + 2 * i + 1);
                    cell2.setCellStyle(colFooterStyle);
                    cell2.setCellValue(sheetFooter.getValue());
                }
            }
        }
        //写入文件
        hssfWorkbook.write(outputStream);
    }

    /**
     * 初始化行样式
     *
     * @param workbook                工作薄对象
     * @param fontName                字体名称：宋体
     * @param fontSize                字号大小：11
     * @param fontBold                是否加粗：true/false
     * @param borderFlag              是否边框：true/false
     * @param alignmentCenter         水平居中：true/false
     * @param verticalAlignmentCenter 垂直居中：true/false
     * @return
     */
    private static HSSFCellStyle createHSSFCellStyle(HSSFWorkbook workbook, String fontName, short fontSize, boolean fontBold, boolean borderFlag, boolean alignmentCenter, boolean verticalAlignmentCenter) {
        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        //--水平居中
        if (alignmentCenter) {
            hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        //--垂直居中
        if (verticalAlignmentCenter) {
            hssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }
        //--创建边框
        if (true == borderFlag) {
            hssfCellStyle.setBorderTop(BorderStyle.THIN);
            hssfCellStyle.setBorderRight(BorderStyle.THIN);
            hssfCellStyle.setBorderBottom(BorderStyle.THIN);
            hssfCellStyle.setBorderLeft(BorderStyle.THIN);
            hssfCellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            hssfCellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        }
        //--创建字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        //--设置字体名称
        font.setFontName(fontName);
        if (null == fontName || fontName.trim().equals("")) {
            font.setFontName("宋体");
        }
        //--设置是否加粗
        font.setBold(fontBold);
        //加载字体
        hssfCellStyle.setFont(font);
        return hssfCellStyle;
    }


    /**
     * 创建Excel文件对象主体
     */
    public static class SheetMain<T> {

        /**
         * 开始行
         */
        private int startRow = 0;

        /**
         * 开始列
         */
        private int startCell = 0;


        private int sheetNum;

        /**
         * 工作表名称
         */
        private String sheetName;

        /**
         * 工作表标题
         */
        private String sheetTitle;

        /**
         * 工作表标题样式
         */
        private CellStyle titleCellStyle = CellStyle.getSingleCellStyle();

        /**
         * 工作表头标题
         */
        private List<String> sheetHeads;

        /**
         * 工作表列宽度
         */
        private List<Integer> cellWidths;

        /**
         * 工作表头标题样式
         */
        private CellStyle headCellStyle = CellStyle.getSingleCellStyle();

        /**
         * 工作表内容主体
         */
        private List<T> Ts;

        /**
         * 工作表内容主体样式
         */
        private CellStyle tCellStyle = CellStyle.getSingleCellStyle();

        /**
         * 工作表页面列表对象
         */
        private List<SheetFooter> sheetFooters;

        /**
         * 工作表脚标内容样式
         */
        private CellStyle footerCellStyle = CellStyle.getSingleCellStyle();

        /**
         * 初始化样式
         *
         * @param startRow        开始行
         * @param startCell       开始列
         * @param cellWidths      列宽
         * @param titleCellStyle  总标题样式
         * @param headCellStyle   列表标题样式
         * @param tCellStyle      列表样式
         * @param footerCellStyle 列表脚标样式
         */
        public void init(Integer startRow, Integer startCell, Integer sheetNum, List<Integer> cellWidths, CellStyle titleCellStyle, CellStyle headCellStyle, CellStyle tCellStyle, CellStyle footerCellStyle) {
            if (null != startRow) {
                this.startRow = startRow;
            }
            if (null != startCell) {
                this.startCell = startCell;
            }
            if (null != sheetNum) {
                this.sheetNum = sheetNum;
            }
            this.cellWidths = cellWidths;
            if (null != titleCellStyle) {
                this.titleCellStyle = titleCellStyle;
            }
            if (null != headCellStyle) {
                this.headCellStyle = headCellStyle;
            }
            if (null != tCellStyle) {
                this.tCellStyle = tCellStyle;
            }
            if (null != footerCellStyle) {
                this.footerCellStyle = footerCellStyle;
            }
        }

        /**
         * 创建样式对象
         *
         * @param fontSize
         * @param fontBold
         * @param borderFlag
         * @param alignmentCenter
         * @param verticalAlignmentCenter
         * @return
         */
        public static CellStyle createCellStyle(Short fontSize, Boolean fontBold, Boolean borderFlag, Boolean alignmentCenter, Boolean verticalAlignmentCenter) {
            CellStyle cellStyle = new CellStyle();
            if (null != fontSize) {
                cellStyle.setFontSize(fontSize);
            }
            if (null != fontBold) {
                cellStyle.setFontBold(fontBold);
            }
            if (null != borderFlag) {
                cellStyle.setBorderFlag(borderFlag);
            }
            if (null != alignmentCenter) {
                cellStyle.setAlignmentCenter(alignmentCenter);
            }
            if (null != verticalAlignmentCenter) {
                cellStyle.setVerticalAlignmentCenter(verticalAlignmentCenter);
            }
            return cellStyle;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public String getSheetTitle() {
            return sheetTitle;
        }

        public void setSheetTitle(String sheetTitle) {
            this.sheetTitle = sheetTitle;
        }

        public List<String> getSheetHeads() {
            return sheetHeads;
        }

        public void setSheetHeads(List<String> sheetHeads) {
            this.sheetHeads = sheetHeads;
        }

        public List<T> getTs() {
            return Ts;
        }

        public void setTs(List<T> ts) {
            Ts = ts;
        }

        public List<SheetFooter> getSheetFooters() {
            return sheetFooters;
        }

        public void setSheetFooters(List<SheetFooter> sheetFooters) {
            this.sheetFooters = sheetFooters;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getStartCell() {
            return startCell;
        }

        public void setStartCell(int startCell) {
            this.startCell = startCell;
        }

        public CellStyle getTitleCellStyle() {
            return titleCellStyle;
        }

        public void setTitleCellStyle(CellStyle titleCellStyle) {
            this.titleCellStyle = titleCellStyle;
        }

        public CellStyle getHeadCellStyle() {
            return headCellStyle;
        }

        public void setHeadCellStyle(CellStyle headCellStyle) {
            this.headCellStyle = headCellStyle;
        }

        public CellStyle gettCellStyle() {
            return tCellStyle;
        }

        public void settCellStyle(CellStyle tCellStyle) {
            this.tCellStyle = tCellStyle;
        }

        public CellStyle getFooterCellStyle() {
            return footerCellStyle;
        }

        public void setFooterCellStyle(CellStyle footerCellStyle) {
            this.footerCellStyle = footerCellStyle;
        }

        public List<Integer> getCellWidths() {
            return cellWidths;
        }

        public void setCellWidths(List<Integer> cellWidths) {
            this.cellWidths = cellWidths;
        }

        public int getSheetNum() {
            return sheetNum;
        }

        public void setSheetNum(int sheetNum) {
            this.sheetNum = sheetNum;
        }
    }

    /**
     * 工作表页脚对象
     */
    public static class SheetFooter {

        /**
         * 名称
         */
        private String name;

        /**
         * 值
         */
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 单元格样式
     */
    private static class CellStyle {

        /**
         * 字体大小
         */
        private short fontSize = 11;

        /**
         * 字体名称
         */
        private String fontName = "宋体";

        /**
         * 是否加粗
         */
        private boolean fontBold = false;

        /**
         * 是否有边框
         */
        private boolean borderFlag = false;

        /**
         * 是否水平居中
         */
        private boolean alignmentCenter = false;

        /**
         * 是否垂直居中
         */
        private boolean verticalAlignmentCenter = true;

        private static CellStyle cellStyle = new CellStyle();

        /**
         * 获取到单独对象
         *
         * @return
         */
        public static CellStyle getSingleCellStyle() {
            return cellStyle;
        }

        public short getFontSize() {
            return fontSize;
        }

        public void setFontSize(short fontSize) {
            this.fontSize = fontSize;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public boolean isFontBold() {
            return fontBold;
        }

        public void setFontBold(boolean fontBold) {
            this.fontBold = fontBold;
        }

        public boolean isBorderFlag() {
            return borderFlag;
        }

        public void setBorderFlag(boolean borderFlag) {
            this.borderFlag = borderFlag;
        }

        public boolean isAlignmentCenter() {
            return alignmentCenter;
        }

        public void setAlignmentCenter(boolean alignmentCenter) {
            this.alignmentCenter = alignmentCenter;
        }

        public boolean isVerticalAlignmentCenter() {
            return verticalAlignmentCenter;
        }

        public void setVerticalAlignmentCenter(boolean verticalAlignmentCenter) {
            this.verticalAlignmentCenter = verticalAlignmentCenter;
        }
    }
}
