package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.appliance.ApplianceDepartmentType;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.appliance.mapper.ApplianceDepartmentTypeMapper;
import cn.muye.erp.appliance.mapper.ApplianceMapper;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import cn.muye.erp.appliance.service.ApplianceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jelynn
 * @date 2017/12/1
 */
@Service
@Transactional
public class ApplianceServiceImpl extends BaseServiceImpl<Appliance> implements ApplianceService {

    @Autowired
    private ApplianceDepartmentTypeMapper applianceDepartmentTypeMapper;
    @Autowired
    private AppliancePackageTypeService appliancePackageTypeService;

    private static final Logger logger = LoggerFactory.getLogger(ApplianceServiceImpl.class);

    private static Logger LOGGER = LoggerFactory.getLogger(ApplianceServiceImpl.class);

    public static final String[] EXCEL_TITLE = {"类型", "类别", "包装类型"};
    @Autowired
    private ApplianceMapper applianceMapper;

    //用户缓存新增的科室类别的code，避免多线程事务问题
    private Map<String, Integer> newDepartmentTypeCodeMap = new HashMap<>();

    private ReentrantLock departmentTypelock = new ReentrantLock();

    //用户缓存新增的包装类型的ID，避免多线程事务问题
    private Map<String, Long> newPackageTypeIdMap = new HashMap<>();

    private ReentrantLock packageTypelock = new ReentrantLock();

    @Override
    public Appliance findApplianceById(Long id) {
        return applianceMapper.findApplianceById(id);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
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
            JSONObject map = JSON.parseObject(queryObj);
            String name = map.getString(SearchConstants.SEARCH_NAME);
            String searchName = map.getString(SearchConstants.SEARCH_SEARCH_NAME);
            String departmentType = map.getString(SearchConstants.SEARCH_DEPARTMENT_TYPE);
            String packageType = map.getString(SearchConstants.SEARCH_PACKAGE_TYPE);
            if (StringUtil.isNotBlank(searchName)) {
                searchName = searchName.toUpperCase();
            }
            return applianceMapper.listApplianceByCondition(name.trim(), searchName, departmentType, packageType, storeId);
        } else {
            return applianceMapper.listAllAppliance(storeId);
        }
    }

    @Override
    public List<Appliance> listBySearchName(String searchName) {
        return applianceMapper.listBySearchName(searchName);
    }

    @Override
    public List<Appliance> findByNameAndCode(String name, int departmentTypeCode) {
        if (StringUtil.isBlank(name) || departmentTypeCode == 0) {
            return Lists.newArrayList();
        }
        Example example = new Example(Appliance.class);
        example.createCriteria().andCondition("DEPARTMENT_TYPE_CODE=" + departmentTypeCode)
                .andCondition("NAME='" + name.trim() + "'")
                .andCondition("DELETE_FLAG=" + Constant.NORMAL);
        return applianceMapper.selectByExample(example);
    }

    @Override
    public int removeById(Long id) {
        Appliance appliance = new Appliance();
        appliance.setId(id);
        appliance.setDeleteFlag(Constant.DELETE);
        appliance.setDeleteTime(new Date());
        return applianceMapper.updateByPrimaryKeySelective(appliance);
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
            return false;
        }
        return true;
    }

    private void createAndSave(Map<String, Object> map) {
        String chName = map.get(EXCEL_TITLE[0]).toString();
        logger.info("器械名称 = " + chName);
        String departmentTypeName = map.get(EXCEL_TITLE[1]).toString();
        logger.info("类别 = " + departmentTypeName);
        int departmentTypeCode = getDepartmentTypeCode(departmentTypeName.trim());
        String packageTypeName = map.get(EXCEL_TITLE[2]).toString();
        logger.info("包装类型 = " + packageTypeName);
        Long packageTypeId = getPackageTypeId(packageTypeName.trim());
        //重复数据校验
        List<Appliance> applianceList = findByNameAndCode(chName, departmentTypeCode);
        if (null != applianceList && applianceList.size() > 0) {
            return;
        }
        Appliance appliance = new Appliance();
        appliance.init();
        appliance.setName(chName);
        appliance.setSearchName(StringUtil.getSearchName(chName));
        appliance.setDepartmentTypeCode(departmentTypeCode);
        appliance.setPackageTypeId(packageTypeId);
        save(appliance);
    }

    private Long getPackageTypeId(String packageTypeName) {
        AppliancePackageType packageType = appliancePackageTypeService.findByName(packageTypeName);
        //如果包装类别不存在，则新增
        Long packageTypeId = null;
        if (null == packageType) {
            try {
                packageTypelock.lock();
                packageTypeId = newPackageTypeIdMap.get(packageTypeName);
                if (null == packageTypeId) {
                    packageType = new AppliancePackageType();
                    packageType.init();
                    packageType.setName(packageTypeName);
                    appliancePackageTypeService.save(packageType);
                    packageTypeId = packageType.getId();
                    newPackageTypeIdMap.put(packageTypeName, packageTypeId);
                }
            } finally {
                packageTypelock.unlock();
            }
        } else {
            packageTypeId = packageType.getId();
        }
        return packageTypeId;
    }

    private int getDepartmentTypeCode(String departmentTypeName) {
        ApplianceDepartmentType departmentType = applianceDepartmentTypeMapper.findByName(departmentTypeName);
        int departmentTypeCode = 0;
        if (null == departmentType) {
            try {
                departmentTypelock.lock();
                Integer departmentTypeCodeObj = newDepartmentTypeCodeMap.get(departmentTypeName);
                if (departmentTypeCodeObj == null) {
                    Integer currentMaxCode = applianceDepartmentTypeMapper.findMaxCode();
                    if (null == currentMaxCode) {
                        departmentTypeCode = 1;
                    } else {
                        departmentTypeCode = currentMaxCode + 1;
                    }

                    departmentType = new ApplianceDepartmentType();
                    departmentType.setCode(departmentTypeCode);
                    departmentType.setName(departmentTypeName);
                    applianceDepartmentTypeMapper.save(departmentType);
                    newDepartmentTypeCodeMap.put(departmentTypeName, departmentTypeCode);
                } else {
                    departmentTypeCode = departmentTypeCodeObj;
                }
            } finally {
                departmentTypelock.unlock();
            }
        } else {
            departmentTypeCode = departmentType.getCode();
        }
        return departmentTypeCode;
    }
}
