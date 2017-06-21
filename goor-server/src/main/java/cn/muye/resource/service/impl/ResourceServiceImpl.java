package cn.muye.resource.service.impl;

import cn.mrobot.bean.resource.Resource;
import cn.muye.resource.mapper.ResourceMapper;
import cn.muye.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Selim on 2017/6/13.
 */
@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public void save(Resource resource) {
        resourceMapper.insert(resource);
    }

    @Override
    public List<Resource> list() {
        return resourceMapper.selectAll();
    }

    @Override
    public List<Resource> listByType(Integer resourceType) {
        Example example = new Example(Resource.class);
        example.createCriteria().andCondition("RESOURCE_TYPE =", resourceType);
        example.setOrderByClause("CREATE_TIME DESC");
        return resourceMapper.selectByExample(example);
    }

    @Override
    public Resource getById(Long resourceId) {
        return resourceMapper.selectByPrimaryKey(resourceId);
    }
}
