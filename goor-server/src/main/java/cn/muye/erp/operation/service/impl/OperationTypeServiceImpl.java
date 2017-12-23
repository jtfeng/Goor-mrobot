package cn.muye.erp.operation.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseCrudServiceImpl;
import cn.muye.erp.operation.mapper.OperationTypeMapper;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import cn.muye.erp.operation.service.OperationTypeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
@Service
public class OperationTypeServiceImpl extends BaseCrudServiceImpl<OperationType> implements OperationTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationTypeServiceImpl.class);
    @Autowired
    private OperationTypeMapper operationTypeMapper;

    @Autowired
    private OperationDepartmentTypeService operationDepartmentTypeService;

    private Map<String, OperationDepartmentType> map = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public OperationType findOperationTypeById(Long id) {
        return operationTypeMapper.findOperationTypeById(id);
    }

    public static final String[] EXCEL_TITLE = {"手术名称", "手术科室"};

    @Override
    public List<OperationType> findByNameAndDepartmentType(String name, Long departmentTypeId) {
        Example example = new Example(OperationType.class);
        example.createCriteria().andCondition("NAME='" + name.trim() + "'")
                .andCondition("DELETE_FLAG=" + Constant.NORMAL)
                .andCondition("OPERATION_DEPARTMENT_TYPE_ID=" + departmentTypeId);
        return operationTypeMapper.selectByExample(example);
    }

    @Override
    public int saveOperationType(OperationType operationType) {
        operationType.setSearchName(operationType.getSearchName().toUpperCase());
        return operationTypeMapper.saveOperationType(operationType);
    }

    @Override
    public void removeById(Long id) {
        OperationType operationType = new OperationType();
        operationType.setId(id);
        operationType.setDeleteFlag(Constant.DELETE);
        operationType.setDeleteTime(new Date());
        operationTypeMapper.updateByPrimaryKeySelective(operationType);
    }

    @Override
    public void updateOperationType(OperationType operationType) {
        operationType.setSearchName(operationType.getSearchName().toUpperCase());
        operationTypeMapper.updateOperationType(operationType);
    }

    @Override
    public List<OperationType> listAllOperationType(WhereRequest whereRequest) {
        String obj = whereRequest.getQueryObj();
        if (StringUtil.isNotBlank(obj)) {
            JSONObject map = JSON.parseObject(obj);
            String name = map.getString(SearchConstants.SEARCH_NAME);
            String searchName = map.getString(SearchConstants.SEARCH_SEARCH_NAME);
            Long operationDepartmentType = map.getLong("operationDepartmentType");
            if (StringUtil.isNotBlank(searchName)) {
                searchName = searchName.toUpperCase();
            }
            return operationTypeMapper.listOperationTypeBy(name, searchName, operationDepartmentType);
        } else {
            return operationTypeMapper.listAllOperationType();
        }
    }

    @Override
    public boolean importExcel(File file) {
        if (!file.exists()) {
            return false;
        }
        return analysisExcel(file);
    }

    @Override
    public List<OperationType> listBySearchName(String searchName) {
        return operationTypeMapper.listBySearchName(searchName);
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
        String operationName = map.get(EXCEL_TITLE[0]).toString();
        logger.info("手术名称 = " + operationName);
        String operationDepartmentTypeName = map.get(EXCEL_TITLE[1]).toString();
        logger.info("手术科室 = " + operationDepartmentTypeName);
        OperationDepartmentType operationDepartmentType = getOperationDepartmentType(operationDepartmentTypeName);
        //校验重复数据
        List<OperationType> operationTypeList = findByNameAndDepartmentType(operationName, operationDepartmentType.getId());
        if (null != operationTypeList && operationTypeList.size() > 0) {
            return;
        }
        OperationType operationType = new OperationType();
        operationType.init();
        operationType.setName(operationName);
        operationType.setSearchName(StringUtil.getSearchName(operationName));
        operationType.setOperationDepartmentType(operationDepartmentType);
        saveOperationType(operationType);
    }

    private OperationDepartmentType getOperationDepartmentType(String operationDepartmentTypeName) {
        OperationDepartmentType departmentType = operationDepartmentTypeService.findByName(operationDepartmentTypeName);
        if (null == departmentType) {
            //如果手术科室不存在，则新增
            try {
                lock.lock();
                departmentType = map.get(operationDepartmentTypeName);
                if (null == departmentType) {
                    departmentType = new OperationDepartmentType();
                    departmentType.init();
                    departmentType.setName(operationDepartmentTypeName);
                    operationDepartmentTypeService.save(departmentType);
                    map.put(operationDepartmentTypeName, departmentType);
                    return departmentType;
                }else {
                    return departmentType;
                }
            } finally {
                lock.unlock();
            }
        } else {
            return departmentType;
        }
    }
}
