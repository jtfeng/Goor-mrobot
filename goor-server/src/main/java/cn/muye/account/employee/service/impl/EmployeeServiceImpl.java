package cn.muye.account.employee.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Employee;
import cn.mrobot.bean.account.EmployeeStationXref;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.FeatureItem;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Goods;
import cn.mrobot.bean.order.GoodsInfo;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.employee.mapper.EmployeeMapper;
import cn.muye.account.employee.mapper.EmployeeStationXrefMapper;
import cn.muye.account.employee.service.EmployeeService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.dispatch.service.FeatureItemService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.order.service.OrderService;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ray.fu on 2017/8/11.
 */
@Service
@Transactional
public class EmployeeServiceImpl extends BaseServiceImpl<Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private EmployeeStationXrefMapper employeeStationXrefMapper;
    @Autowired
    private StationService stationService;
    @Autowired
    private MissionItemTaskService missionItemTaskService;
    @Autowired
    private FeatureItemService featureItemService;
    @Autowired
    private MissionListTaskService missionListTaskService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserUtil userUtil;

    private static Logger LOGGER = Logger.getLogger(EmployeeServiceImpl.class);

    @Override
    public void addEmployee(Employee employee) throws RuntimeException {
        employee.setCreatedBy(userUtil.getCurrentUserId());
        employee.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        employee.setCreateTime(new Date());
        employeeMapper.insert(employee);
        List<Station> list = employee.getStationList();
        List<Station> stationListNew = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            for (Station station : list) {
                EmployeeStationXref xref = new EmployeeStationXref();
                xref.setEmployeeId(employee.getId());
                xref.setStationId(station.getId());
                employeeStationXrefMapper.insert(xref);
                Station stationDb = stationService.findById(station.getId());
                stationListNew.add(stationDb);
            }
        }
        employee.setStationList(stationListNew);
    }

    @Override
    public void updateEmployee(Employee employee) throws RuntimeException {
        super.updateSelectiveByStoreId(employee);
        Long empId = employee.getId();
        Example example = new Example(EmployeeStationXref.class);
        example.createCriteria().andCondition("EMPLOYEE_ID=", empId);
        employeeStationXrefMapper.deleteByExample(example);
        List<Station> stationList = employee.getStationList();
        List<Station> stationListNew = Lists.newArrayList();
        if (stationList != null && stationList.size() > 0) {
            for (Station station : stationList) {
                EmployeeStationXref xref = new EmployeeStationXref();
                xref.setStationId(station.getId());
                xref.setEmployeeId(empId);
                employeeStationXrefMapper.insert(xref);
                Station stationDb = stationService.findById(station.getId());
                stationListNew.add(stationDb);
            }
        }
        employee.setStationList(stationListNew);
    }

    @Override
    public Employee getByCode(String code) throws RuntimeException {
//        Example example = new Example(Employee.class);
//        Example.Criteria criteria = example.createCriteria().andCondition("CODE = ", code);
//        criteria.andCondition("STORE_ID = ", SearchConstants.FAKE_MERCHANT_STORE_ID);
        Employee employee = new Employee();
        employee.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        employee.setCode(code);
//        List<Employee> list = employeeMapper.selectByExample(example);
        Employee employeeDb = employeeMapper.selectOne(employee);
//        if (list != null && list.size() > 0) {
//            return list.get(0);
//        } else {
//            return null;
//        }
        return employeeDb;
    }

    @Override
    public Employee getByCodeType(String code,Integer type) {
        Employee employee = new Employee();
        employee.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        employee.setCode(code);
        employee.setType(type);
        List<Employee> employeeDb = employeeMapper.select(employee);
        if(employeeDb == null || employeeDb.size() == 0) {
            return null;
        }
        return employeeDb.get(0);
    }

    @Override
    public List<Employee> list(WhereRequest whereRequest) {
        List<Employee> employeeDbList = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Employee.class, "ID DESC");
        if (employeeDbList != null && employeeDbList.size() > 0) {
            for (Employee employee : employeeDbList) {
                Example example = new Example(Employee.class);
                example.createCriteria().andCondition("EMPLOYEE_ID=", employee.getId());
                List<EmployeeStationXref> listDb = employeeStationXrefMapper.selectByExample(example);
                List<Station> stationList = Lists.newArrayList();
                if (listDb != null && listDb.size() > 0) {
                    for (EmployeeStationXref xref : listDb) {
                        Station stationDb = stationService.findById(xref.getStationId());
                        stationList.add(stationDb);
                    }
                }
                employee.setStationList(stationList);
            }
        }
        return employeeDbList;
    }

    @Override
    public AjaxResult verifyEmplyeeNumber(String code, Long missionItemId, String subName) throws Exception {
        MissionItemTask missionItemTaskListDb = missionItemTaskService.findById(missionItemId);
        //记录日志.单独开前程，避免影响主线程
        new Thread(() -> saveLogInfo(code, missionItemTaskListDb)).start();
        if (subName.equals(TopicConstants.VERIFY_ELEVATOR_ADMIN_NUMBER)) {
            Employee employee = new Employee();
            employee.setType(Constant.EMPLOYEE_TYPE_ELEVATOR_ADMIN);
            employee.setCode(code);
            List<Employee> list = employeeMapper.select(employee);
            List<String> codeList = Lists.newArrayList();
            list.forEach(oneEmployee -> {
                codeList.add(oneEmployee.getCode());
            });
            if (list != null && list.size() > 0 && codeList.contains(code)) {
                return AjaxResult.success("校验成功");
            } else {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "校验失败");
            }
        } else if (subName.equals(TopicConstants.VERIFY_EMPLYEE_NUMBER)) {
            Employee employee = new Employee();
            employee.setCode(code);
//            employee.setActivated(true);
            Employee employeeDb = employeeMapper.selectOne(employee);
            if (employeeDb == null) {
                LOGGER.info("##EmployeeServiceImpl verifyEmplyeeNumber : employee " + code + " not found");
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "输入信息错误");
            } else {
                if (missionItemTaskListDb != null && Constant.MISSION_ITEM_TASK_NOT_CONCERN_STATION_NAMES_FOR_EMP_NUMBER.contains(missionItemTaskListDb.getName())) {
                    Map map = Maps.newHashMap();
                    map.put("code", code);
                    map.put("missionItemId", missionItemId);
                    List<Employee> employeeListDb = employeeMapper.selectEmployeeNumberByMissionItemId(map);
                    if (employeeListDb != null && employeeListDb.size() > 0) {
                        return AjaxResult.success("校验成功");
                    } else {
                        LOGGER.info("##EmployeeServiceImpl verifyEmplyeeNumber : employee " + code + " 没有权限");
                        return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "没有权限");
                    }
                } else {
                    LOGGER.info("##EmployeeServiceImpl verifyEmplyeeNumber : missionItemTaskListDb " + missionItemId + " not found or wrong missionItemTaskType");
                }
            }
        }
        return null;
    }

    @Override
    public List<String> listAvailableEmployees(Long stationId, Integer employeeType) {
        return employeeMapper.listAvailableEmployees(stationId, employeeType);
    }

    private void saveLogInfo(String code, MissionItemTask missionItemTaskDb) {
        if (missionItemTaskDb == null) {
            return;
        }
        String featureValue = missionItemTaskDb.getFeatureValue();
        FeatureItem featureItem = featureItemService.findByValue(featureValue);
        if (featureItem == null) {
            return;
        }

        String featureItemName = featureItem.getName();
        String message = "";
        if (Constant.UNLOAD.equals(featureItemName)) {
            message = getGoodsInfo(missionItemTaskDb.getData());
        }else if (Constant.FINAL_UNLOAD.equals(featureItemName)){
            message = getShelfName(missionItemTaskDb.getMissionListId());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("工号 " + code)
                .append(" " + featureItemName)
                .append(" " + message);
        LogInfoUtils.info("工号" + code, ModuleEnums.MISSION, LogType.INFO_EXECUTE_TASK, stringBuilder.toString());
    }

    private String getGoodsInfo(String data) {
        JSONObject object = JSON.parseObject(data);
        List<GoodsInfo> goodsInfoList = JSONArray.parseArray(object.getString("goodsInfos"), GoodsInfo.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (GoodsInfo goodsInfo : goodsInfoList) {
            Goods goods = goodsInfo.getGoods();
            stringBuilder.append(goods.getName() + " ")
                    .append(goodsInfo.getNum())
                    .append(" " + goods.getUnit() + ",");
        }
        return stringBuilder.toString();
    }

    private String getShelfName(Long missionListId){
        MissionListTask missionListTask = missionListTaskService.findById(missionListId);
        if (null == missionListTask){
            return "";
        }
        Order order = orderService.getOrder(missionListTask.getOrderId());
        if (null == order){
            return "";
        }
        Shelf shelf = order.getShelf();
        if (null == shelf){
            return "";
        }
        return shelf.getName();
    }
}
