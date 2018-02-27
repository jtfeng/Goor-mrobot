package cn.muye.erp.operation.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.operation.OperationDepartmentType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseCrudServiceImpl;
import cn.muye.erp.operation.mapper.OperationDepartmentTypeMapper;
import cn.muye.erp.operation.service.OperationDepartmentTypeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
@Service
public class OperationDepartmentTypeServiceImpl extends BaseCrudServiceImpl<OperationDepartmentType> implements OperationDepartmentTypeService {

    @Autowired
    private OperationDepartmentTypeMapper operationDepartmentTypeMapper;

    @Override
    public OperationDepartmentType findTypeById(Long id) {
        OperationDepartmentType departmentType = new OperationDepartmentType.Builder().id(id).deleteFlag(Constant.NORMAL).build();
        return operationDepartmentTypeMapper.selectOne(departmentType);
    }

    @Override
    public List<OperationDepartmentType> listAllType() {
        Example example = new Example(OperationDepartmentType.class);
        example.createCriteria().andCondition("DELETE_FLAG=" + Constant.NORMAL);
        example.setOrderByClause("CREATE_TIME desc, ID DESC");
        return operationDepartmentTypeMapper.selectByExample(example);
    }

    @Override
    public List<OperationDepartmentType> listAllType(WhereRequest whereRequest) {
        String obj = whereRequest.getQueryObj();
        if (StringUtil.isNotBlank(obj)) {
            JSONObject map = JSON.parseObject(obj);
            String name = map.getString(SearchConstants.SEARCH_NAME);
            return findByNameLike(name.trim());
        } else {
            return listAllType();
        }
    }

    private List<OperationDepartmentType> findByNameLike(String name) {
        Example example = new Example(OperationDepartmentType.class);
        example.createCriteria().andCondition("NAME like '%" + name + "%'");
        example.setOrderByClause("CREATE_TIME desc");
        return operationDepartmentTypeMapper.selectByExample(example);
    }

    @Override
    public int removeById(Long id) {
        OperationDepartmentType departmentType =
                new OperationDepartmentType.Builder().id(id).deleteFlag(Constant.DELETE).deleteTime(new Date()).build();
        return operationDepartmentTypeMapper.updateByPrimaryKeySelective(departmentType);
    }

    @Override
    public OperationDepartmentType findByName(String name) {
        OperationDepartmentType departmentType =
                new OperationDepartmentType.Builder().name(name.trim()).deleteFlag(Constant.NORMAL).build();
        return operationDepartmentTypeMapper.selectOne(departmentType);
    }
}
