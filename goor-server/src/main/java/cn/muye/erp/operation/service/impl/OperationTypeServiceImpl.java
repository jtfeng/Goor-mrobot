package cn.muye.erp.operation.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.operation.OperationDefaultApplianceXREF;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.utils.ExcelUtil;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseCrudServiceImpl;
import cn.muye.erp.appliance.service.ApplianceService;
import cn.muye.erp.operation.mapper.OperationDefaultApplianceXREFMapper;
import cn.muye.erp.operation.mapper.OperationTypeMapper;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import cn.muye.erp.operation.service.OperationTypeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    @Autowired
    private ApplianceService applianceService;

    @Autowired
    private OperationDefaultApplianceXREFMapper operationDefaultApplianceXREFMapper;

    private Map<String, OperationDepartmentType> operationDepartmentTypeMap = new HashMap<>();
    private ReentrantLock operationDepartmentTypeLock = new ReentrantLock();

    private Map<String, OperationType> operationTypeMap = new HashMap<>();
    private ReentrantLock operationTypeLock = new ReentrantLock();

    private Map<String, Appliance> applianceMap = new HashMap<>();
    private ReentrantLock applianceLock = new ReentrantLock();

    @Override
    public OperationType findOperationTypeById(Long id) {
        return operationTypeMapper.findOperationTypeById(id);
    }

    //EXCEL 导入文件头
    public static final String[] EXCEL_TITLE = {"手术名称", "手术科室"};
    public static final String[] OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE = {"手术室编号", "手术名称", "手术科室", "器械名称", "器械包装","数量"};

    private static final String EXCEL_DATE_OPERATION_TYPE = "OperationType";
    private static final String EXCEL_DATE_OPERATION_DEFAULT_APPLIANCE = "OperationDefaultAppliance";

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
        return analysisExcel(file, EXCEL_DATE_OPERATION_TYPE);
    }

    @Override
    public List<OperationType> listBySearchName(String searchName) {
        return operationTypeMapper.listBySearchName(searchName);
    }

    @Override
    public List<OperationType> listByDepartmentTypeId(Long id) {
        return operationTypeMapper.listByDepartmentTypeId(id);
    }

    @Override
    public boolean importOperationDefaultApplianceExcel(File file) {
        if (!file.exists()) {
            return false;
        }
        return analysisExcel(file, EXCEL_DATE_OPERATION_DEFAULT_APPLIANCE);
    }

    private boolean analysisExcel(File file, String type) {
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
                    if (EXCEL_DATE_OPERATION_TYPE.equals(type)) {
                        createAndSaveOperationType(map);
                    } else if (EXCEL_DATE_OPERATION_DEFAULT_APPLIANCE.equals(type)) {
                        createAndSaveOperationDefaultAppliance(map);
                    }
                    });
                }
            }
        } catch (Exception e) {
            LOGGER.error("解析文件出错", e);
            return false;
        }
        return true;
    }

    private void createAndSaveOperationDefaultAppliance(Map<String, Object> map) {
        String operationRoomNumber = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[0]).toString();
        String operationName = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[1]).toString();
        String operationDepartment = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[2]).toString();
        logger.info("手术室编号 = " + operationRoomNumber + "手术名称 = " + operationName + "手术科室 = " + operationDepartment);
        String applianceName = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[3]).toString();
        String appliancePackageType = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[4]).toString();
        String applianceNumber = map.get(OPERATION_DEFAULT_APPLIANCE_EXCEL_TITLE[5]).toString();
        logger.info("器械名称 = " + applianceName + "器械包装 = " + appliancePackageType+ "数量 = " + applianceNumber);
        if (StringUtil.isBlank(operationName) || StringUtil.isBlank(applianceName)) {
            return;
        }
        OperationType operationType = findOrAddOperationTypeByName(operationRoomNumber, operationName, operationDepartment);
        Appliance appliance = findOrAddApplianceByName(applianceName, appliancePackageType);
        int number = 0;
        if (StringUtil.isNotBlank(applianceNumber)){
            //如果是小数形式，取整
            number = Integer.parseInt(applianceNumber.split("\\.")[0]);
        }
        saveOperationDefaultAppliance(operationType, appliance, number);
    }

    /**
     * 查询或新增手术类型
     * @param operationRoomNumber
     * @param operationName
     * @param operationDepartment
     * @return
     */
    private OperationType findOrAddOperationTypeByName(String operationRoomNumber, String operationName, String operationDepartment) {
        OperationDepartmentType departmentType = getOperationDepartmentType(operationDepartment);
        List<OperationType> operationTypeList = findByNameAndDepartmentType(operationName, departmentType.getId());
        OperationType operationType;
        if (null != operationTypeList && operationTypeList.size() > 0){
            operationType = operationTypeList.get(0);
            operationType.setOperationRoomNumber(operationRoomNumber);
            operationTypeMapper.updateByPrimaryKeySelective(operationType);
        }else {
            //手术名称不存在则新增
            try {
                operationTypeLock.lock();
                operationType = operationTypeMap.get(operationName);
                if (null == operationType) {
                    operationType = saveOperationType(operationName, departmentType, operationRoomNumber);
                    operationTypeMap.put(operationName, operationType);
                }
            }finally {
                operationTypeLock.unlock();
            }
        }
        return operationType;
    }

    /**
     * 查询或新增额外器械
     * @param applianceName
     * @param appliancePackageType
     * @return
     */
    private Appliance findOrAddApplianceByName(String applianceName, String appliancePackageType) {
        Long packageTypeId = applianceService.getPackageTypeId(appliancePackageType);
        Appliance appliance = new Appliance();
        appliance.setName(applianceName);
        appliance.setPackageTypeId(packageTypeId);
        Appliance applianceDB = applianceService.findOne(appliance);
        if (null == applianceDB){
            //如果不存在则新增
            try {
                applianceLock.lock();
                applianceDB = applianceMap.get(applianceName);
                if (null == applianceDB) {
                    appliance.init();
                    appliance.setSearchName(StringUtil.getSearchName(applianceName));
                    applianceService.save(appliance);
                    applianceMap.put(applianceName, appliance);
                }else {
                    appliance = applianceDB;
                }
            }finally {
                applianceLock.unlock();
            }
        }else {
            appliance = applianceDB;
        }
        return appliance;
    }

    /**
     * 保存手术类型和额外器械的关联关系
     * @param operationType
     * @param appliance
     * @param applianceNumber
     */
    private void saveOperationDefaultAppliance(OperationType operationType, Appliance appliance, int applianceNumber) {
        OperationDefaultApplianceXREF xref = new OperationDefaultApplianceXREF();
        xref.setOperationTypeId(operationType.getId());
        xref.setAppliance(appliance);
        xref.setNumber(applianceNumber);
        operationDefaultApplianceXREFMapper.save(xref);
    }

    private void createAndSaveOperationType(Map<String, Object> map) {
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
        saveOperationType(operationName, operationDepartmentType, null);
    }

    private OperationType saveOperationType(String operationName, OperationDepartmentType operationDepartmentType, String operationRoomNumber){
        OperationType operationType = new OperationType();
        operationType.init();
        operationType.setName(operationName);
        operationType.setSearchName(StringUtil.getSearchName(operationName));
        operationType.setOperationDepartmentType(operationDepartmentType);
        operationType.setOperationRoomNumber(operationRoomNumber);
        saveOperationType(operationType);
        return operationType;
    }

    private OperationDepartmentType getOperationDepartmentType(String operationDepartmentTypeName) {
        OperationDepartmentType departmentType = operationDepartmentTypeService.findByName(operationDepartmentTypeName);
        if (null == departmentType) {
            //如果手术科室不存在，则新增
            try {
                operationDepartmentTypeLock.lock();
                departmentType = operationDepartmentTypeMap.get(operationDepartmentTypeName);
                if (null == departmentType) {
                    departmentType = new OperationDepartmentType();
                    departmentType.init();
                    departmentType.setName(operationDepartmentTypeName);
                    operationDepartmentTypeService.save(departmentType);
                    operationDepartmentTypeMap.put(operationDepartmentTypeName, departmentType);
                    return departmentType;
                } else {
                    return departmentType;
                }
            } finally {
                operationDepartmentTypeLock.unlock();
            }
        } else {
            return departmentType;
        }
    }
}
