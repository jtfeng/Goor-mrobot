package cn.muye.erp.appliance.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.erp.appliance.AppliancePackageType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.erp.appliance.mapper.AppliancePackageTypeMapper;
import cn.muye.erp.appliance.service.AppliancePackageTypeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/5
 */
@Service
public class AppliancePackageTypeServiceImpl extends BaseServiceImpl<AppliancePackageType> implements AppliancePackageTypeService {

    @Autowired
    private AppliancePackageTypeMapper appliancePackageTypeMapper;

    @Override
    public AppliancePackageType findByName(String name) {
        AppliancePackageType appliancePackageType = new AppliancePackageType.Builder().name(name).deleteFlag(Constant.NORMAL).build();
        List<AppliancePackageType> appliancePackageTypes = appliancePackageTypeMapper.select(appliancePackageType);
        if (appliancePackageTypes.size() > 0) {
            return appliancePackageTypes.get(0);
        }
        return null;
    }

    @Override
    public AppliancePackageType findTypeById(Long id) {
        AppliancePackageType appliancePackageType = new AppliancePackageType.Builder().id(id).deleteFlag(Constant.NORMAL).build();
        return appliancePackageTypeMapper.selectOne(appliancePackageType);
    }

    @Override
    public List<AppliancePackageType> listAllPackageType() {
        AppliancePackageType appliancePackageType = new AppliancePackageType.Builder().deleteFlag(Constant.NORMAL).build();
        return appliancePackageTypeMapper.select(appliancePackageType);
    }

    @Override
    public int removeById(Long id) {
        AppliancePackageType appliancePackageType =
                new AppliancePackageType.Builder().id(id).deleteFlag(Constant.DELETE).deleteTime(new Date()).build();
        return appliancePackageTypeMapper.updateByPrimaryKeySelective(appliancePackageType);
    }

    @Override
    public List<AppliancePackageType> listAllPackageType(WhereRequest whereRequest) {
        String obj = whereRequest.getQueryObj();
        if (StringUtil.isNotBlank(obj)) {
            JSONObject map = JSON.parseObject(obj);
            String name = map.getString(SearchConstants.SEARCH_NAME);
            return appliancePackageTypeMapper.listByName(name);
        } else {
            return listAllPackageType();
        }
    }

}
