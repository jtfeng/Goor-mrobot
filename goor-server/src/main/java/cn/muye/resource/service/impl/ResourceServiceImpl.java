package cn.muye.resource.service.impl;

import cn.mrobot.bean.resource.Resource;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.resource.service.ResourceService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Selim on 2017/6/13.
 */
@Service
@Transactional
public class ResourceServiceImpl extends BaseServiceImpl<Resource> implements ResourceService {

    @Override
    public List<Resource> listByType(Integer resourceType, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        Example example = new Example(Resource.class);
        example.createCriteria().andCondition("RESOURCE_TYPE =", resourceType);
        example.setOrderByClause("CREATED DESC");
        return myMapper.selectByExample(example);
    }

}
