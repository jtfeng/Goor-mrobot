package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.DepartmentType;
import cn.mrobot.bean.erp.appliance.PackageType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.appliance.mapper.ApplianceMapper;
import cn.muye.erp.appliance.mapper.DepartmentTypeMapper;
import cn.muye.erp.appliance.mapper.PackageTypeMapper;
import cn.muye.erp.appliance.service.ApplianceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@Service
@Transactional
public class ApplianceServiceImpl extends BaseServiceImpl<Appliance> implements ApplianceService {

    @Autowired
    private DepartmentTypeMapper departmentTypeMapper;
    @Autowired
    private PackageTypeMapper packageTypeMapper;

    private static final Logger logger = LoggerFactory.getLogger(ApplianceServiceImpl.class);

    private static Logger LOGGER = LoggerFactory.getLogger(ApplianceServiceImpl.class);

    private static final String[] EXCEL_TITLE = {"类型", "类别", "包装类型"};
    @Autowired
    private ApplianceMapper applianceMapper;

    @Override
    public Appliance findApplianceById(Long id) {
        return applianceMapper.findApplianceById(id);
    }

    @Override
    public boolean importExcel(File file) {
        if (!file.exists()) {
            return false;
        }
        return analysisExcel(file);
    }

    @Override
    public List<Appliance> lists(WhereRequest whereRequest) {
        Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
        String queryObj = whereRequest.getQueryObj();
        if (StringUtil.isNotBlank(queryObj)) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
            String name = map.getString(SearchConstants.SEARCH_NAME);
            String searchName = map.getString(SearchConstants.SEARCH_SEARCH_NAME);
            String departmentType = map.getString(SearchConstants.SEARCH_DEPARTMENT_TYPE);
            String packageType = map.getString(SearchConstants.SEARCH_PACKAGE_TYPE);
            return applianceMapper.listApplianceByCondition(name, searchName, departmentType, packageType, storeId);
        } else {
            return applianceMapper.listAllAppliance(storeId);
        }
    }

    @Override
    public List<Appliance> listBySearchName(String searchName, Long storeId) {
        if (searchName.length() < 3) {
            return applianceMapper.listBySearchNameTwo(searchName, storeId);
        } else {
            return applianceMapper.listBySearchNameThree(searchName, storeId);
        }
    }

    @Override
    public List<Appliance> findByNameAndCode(String name, int departmentTypeCode, int packageTypeCode) {
        return applianceMapper.findByNameAndCode(name, departmentTypeCode, packageTypeCode);
    }

    private boolean analysisExcel(File file) {
        try {
            Map<String, List<Map<String, Object>>> result = ExcelUtil.getTableSheetData(file);
            Iterator<Map.Entry<String, List<Map<String, Object>>>> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<Map<String, Object>>> entry = iterator.next();
                String key = entry.getKey();
                logger.info("读取工作表 " + key + " 中的数据");
                List<Map<String, Object>> entryValue = entry.getValue();
                int listSize = entryValue.size();
                logger.info("记录条数 " + listSize);
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                for (int i = 0; i < listSize; i++) {
                    Map<String, Object> map = entryValue.get(i);
                    executorService.execute(() -> {
                        createAndSave(map);
                    });
                }
            }
        } catch (Exception e) {
            LOGGER.error("解析文件出错", e);
        }
        return false;
    }

    private void createAndSave(Map<String, Object> map) {
        String chName = map.get(EXCEL_TITLE[0]).toString();
        String departmentTypeName = map.get(EXCEL_TITLE[1]).toString();
        logger.info("departmentTypeName = " + departmentTypeName);
        DepartmentType departmentType = departmentTypeMapper.findByName(departmentTypeName);
        int departmentTypeCode = departmentType.getCode();
        String packageTypeName = map.get(EXCEL_TITLE[2]).toString();
        logger.info("packageTypeName = " + departmentTypeName);
        PackageType packageType = packageTypeMapper.findByName(packageTypeName);
        int packageTypeCode = packageType.getCode();
        //重复数据校验
        List<Appliance> applianceList = findByNameAndCode(chName, departmentTypeCode, packageTypeCode);
        if (null != applianceList || applianceList.size() > 0){
            return;
        }
        Appliance appliance = new Appliance();
        appliance.setName(chName);
        appliance.setSearchName(getSearchName(chName));
        appliance.setDepartmentTypeCode(departmentTypeCode);
        appliance.setPackageTypeCode(packageTypeCode);
        appliance.setCreateTime(new Date());
        appliance.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        save(appliance);
    }

    /**
     * 根据名称获取searchName
     *
     * @param name
     * @return
     */
    private String getSearchName(String name) {
        int length = name.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = name.charAt(i);
            String chStr = String.valueOf(ch);
            if (StringUtil.isNumeric(chStr)) {
                stringBuilder.append(chStr);
            } else if (StringUtil.isCharacter(chStr)) {
                stringBuilder.append(chStr.toUpperCase());
            } else if (StringUtil.isContainChinese(chStr)) {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(name.charAt(i));
                if (null != pinyins) {
                    //中文多音字默认取第一个拼音
                    String pinyin = pinyins[0];
                    char pinyinCh = pinyin.charAt(0);
                    stringBuilder.append(String.valueOf(pinyinCh).toUpperCase());
                }
            } else {
                continue;
            }
        }
        return stringBuilder.toString();
    }
}
