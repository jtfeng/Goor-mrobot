package cn.mrobot.utils;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用POI解析和生成excel，暂不支持单元格合并情况，默认是标准格式的excel
 *
 * @author Jelynn
 * @date 2017/12/4
 */
public class ExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    private static final String SUFFIX_2007 = "xls";
    private static final String SUFFIX_2013 = "xlsx";

    /**
     * 读取excel表中的数据
     * <p>
     * map的key为工作表名称，list为工作表中列名和数据的键值对
     *
     * @param file
     * @return
     */
    public static Map<String, List<Map<String, Object>>> getTableSheetData(File file) {
        if (!file.exists()) {
            return null;
        }
        return getExcelData(file);
    }

    /**
     * 读取excel表的表头，验证excel模板
     *
     * @param file
     * @return
     */
    public static List<String> getTableSheetDeader(File file) {
        if (!file.exists()) {
            return null;
        }
        List<String> headerList = new ArrayList<>();
        Row headerRow = getTableSheetDeaderRow(file);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headerList.add(headerRow.getCell(i).toString());
        }
        return headerList;
    }

    public static boolean isExcelFile(String fileName) {
        return fileName.endsWith(SUFFIX_2007) || fileName.endsWith(SUFFIX_2013);
    }

    private static Row getTableSheetDeaderRow(File file) {
        try {
            Workbook workbook = WorkbookFactory.create(file);
            if (null == workbook) {
                log.error("未知文件格式");
                return null;
            }
            //获取工作表数量
            int sheetNumber = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNumber; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                return sheet.getRow(0);
            }
        } catch (Exception e) {
            log.error("解析文件出错", e);
        }
        return null;
    }

    private static Map<String, List<Map<String, Object>>> getExcelData(File file) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file);
            if (null == workbook) {
                log.error("未知文件格式");
                return null;
            }
            Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
            //获取工作表数量
            int sheetNumber = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNumber; i++) {
                List<Map<String, Object>> sheet_data = new ArrayList<Map<String, Object>>();
                Sheet sheet = workbook.getSheetAt(i);
                if (ifSheetNullOrEmpty(sheet)) {
                    continue;
                }
                //工作表名称
                String sheet_name = workbook.getSheetName(i);
                int num = sheet.getLastRowNum();
                log.info("工作表" + sheet_name + " 数据有" + num + "行");
                for (int j = 1; j <= num; j++) {
                    Row row = sheet.getRow(j);
                    if (ifRowNullOrEmpty(row)) {
                        continue;
                    }
                    Map<String, Object> record = new HashMap<String, Object>();
                    //获取表头
                    Row first = sheet.getRow(0);
                    getRowData(row, record, first);
                    sheet_data.add(record);
                }
                result.put(sheet_name, sheet_data);
            }
            return result;
        } catch (Exception e) {
            log.error("解析文件出错", e);
        }finally {
            if (null != workbook){
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error("解析文件出错", e);
                }
            }
        }
        return null;
    }

    /**
     * 此处有个点要注意,getLastCellNum,下标是从1开始,有多少列,这里就是这个值.而getLastRowNum,下标是从0开始,也就是21行的表格,这里获得的值是20.用户可自行验证.
     *
     * @param row    该行记录
     * @param record 返回值
     * @param first  表头
     */
    private static void getRowData(Row row, Map<String, Object> record, Row first) {
        for (int k = 0; k < row.getLastCellNum(); k++) {
            String value = "";
            Cell cell = row.getCell(k);
            CellType cellType = cell.getCellTypeEnum();
            if (cellType.equals(CellType.BOOLEAN)) {
                value = cell.getBooleanCellValue() + "";
            } else if (cellType.equals(CellType.NUMERIC)) {
                value = cell.getNumericCellValue() + "";
            } else if (cellType.equals(CellType.STRING)) {
                value = cell.getStringCellValue();
            }
            record.put(first.getCell(k).toString(), value);
        }
    }

    public static boolean ifRowNullOrEmpty(Row row) {
        if (row == null || row.getLastCellNum() == 0 || row.getCell(0) == null) {
            return true;
        }
        //遍历每个元素，如果都为空，返回false
        int length = row.getLastCellNum();
        //记录改行表格内容为空的总量
        int count = 0;
        for (int i = 0; i <length; i ++){
            Cell cell = row.getCell(i);
            if (null == cell || StringUtil.isBlank(cell.toString())){
                count ++;
            }
        }
        //如果该行表格中列内容为空的数量等于该行的列的长度，则说明该行为空
        if (count == length){
            return true;
        }
        return false;
    }

    public static boolean ifSheetNullOrEmpty(Sheet sheet) {
        if (sheet == null || sheet.getLastRowNum() == 0) {
            return true;
        }
        return false;
    }

    private static boolean validExcelFormat(String fileName, String type) {
        if (getOS().contains("win")) {
            return getFileNameSuffix(fileName).equalsIgnoreCase(type);
        } else if (getOS().contains("linux")) {
            return getFileNameSuffix(fileName).equals(type);
        } else {
            return false;
        }
    }

    private static String getFileNameSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("." + 1, fileName.length()));
    }

    private static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static void main(String[] args) {
        File file = new File("D:\\test.xlsx");
        Map<String, List<Map<String, Object>>> result = getTableSheetData(file);
        log.info("Map<String, List<Map<String, Object>>>Map<String, List<Map<String, Object>>>");
    }
}
