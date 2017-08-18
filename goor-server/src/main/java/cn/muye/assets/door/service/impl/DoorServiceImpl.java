package cn.muye.assets.door.service.impl;

import cn.mrobot.bean.assets.door.Door;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.door.mapper.DoorMapper;
import cn.muye.assets.door.service.DoorService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DoorServiceImpl extends BaseServiceImpl<Door> implements DoorService {

    private static final Logger log = LoggerFactory.getLogger(DoorServiceImpl.class);
    @Autowired
    private DoorMapper myMapper;

    @Override
    public int save(Door door) {
        return myMapper.insert(door);
    }

    @Override
    public int update(Door door) {
        return myMapper.updateByPrimaryKeySelective(door);
    }

    @Override
    public Door findById(long id, long storeId,Long sceneId) {
        Example example = new Example(Door.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ID =", id)
                .andCondition("ACTIVE =", Constant.NORMAL)
                .andCondition("STORE_ID =", storeId);
        if(sceneId != null) {
            criteria.andCondition("SCENE_ID =", sceneId);
        }
        example.setOrderByClause("ID DESC");
        List<Door> temp = myMapper.selectByExample(example);
        if (temp == null || temp.size() <= 0) {
            return null;
        }
        return temp.get(0);
    }

    @Override
    public List<Door> list(WhereRequest whereRequest, Long storeId,Long sceneId) {
        //如果whereRequest不为null，则分页
        if (whereRequest != null) {
            PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        }

        List<Door> stationList = new ArrayList<Door>();
        if (whereRequest != null && whereRequest.getQueryObj() != null && JSON.parseObject(whereRequest.getQueryObj()) != null) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());

            //TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
            /*result = myMapper.list(name);*/

            //方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
            Example example = new Example(Door.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andCondition("ACTIVE =", Constant.NORMAL)
                    .andCondition("STORE_ID =", storeId);
            Object name = map.get(SearchConstants.SEARCH_NAME);
            if(name != null) {
                criteria.andCondition("NAME like", "%" + name + "%");
            }
            if(sceneId != null) {
                criteria.andCondition("SCENE_ID =", sceneId);
            }
            example.setOrderByClause("ID DESC");
            stationList = myMapper.selectByExample(example);
        } else {
            //TODO 方法一：　测试用多表联查查数据库,缺点是pageHelper分页条数会按照leftjoin查询条数去算，不准确
			/*result = myMapper.list(null);*/

            //方法二：用公共mapper逐条查询，然后再for循环遍历关系表得到point序列，再更新到对象中
            Example example = new Example(Door.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andCondition("ACTIVE =", Constant.NORMAL);
            example.setOrderByClause("ID DESC");
            //超级管理员传storeId=null，能查看所有站；医院管理员传storeId!=null，只能查看该医院的站
            if (storeId != null) {
                criteria.andCondition("STORE_ID =", storeId);
            }
            if(sceneId != null) {
                criteria.andCondition("SCENE_ID =", sceneId);
            }
            stationList = myMapper.selectByExample(example);
        }

        return stationList;
    }

    /**
     * 从数据库查出对应点
     * @param doorList
     * @return
     */
    private List<Door> listSetPoints(List<Door> doorList) {
        for(Door door:doorList) {

        }
        return doorList;
    }

    @Override
    public List<Door> listByName(String name,long storeId,long sceneId) {
        Example example = new Example(Door.class);
        example.createCriteria().andCondition("NAME =", name)
                .andCondition("SCENE_ID =", sceneId)
                .andCondition("STORE_ID =", storeId)
                .andCondition("ACTIVE =", Constant.NORMAL);
        return myMapper.selectByExample(example);
    }

    @Override
    public int delete(Door door) {
        return myMapper.delete(door);
    }

}
