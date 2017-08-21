package cn.muye.assets.door.service;

import cn.mrobot.bean.assets.door.Door;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

public interface DoorService extends BaseService<Door> {
    int save(Door door);

    int update(Door door);

    Door findById(long id, long storeId,Long sceneId);

    /**
     * 查询门列表
     * @param whereRequest
     * @param storeId 超级管理员传storeId=null，能查看所有站；医院管理员传storeId!=null，只能查看该医院的站
     * @param sceneId sceneId=null，不按场景过滤
     * @return
     */
    List<Door> list(WhereRequest whereRequest, Long storeId, Long sceneId);

    /**
     * 根据名称查看门列表
     * @param name
     * @param storeId
     * @param sceneId
     * @return
     */
    List<Door> listByName(String name, long storeId,Long sceneId);

    /**
     * 根据等待点查看门列表
     * @param waitPointId
     * @param storeId
     * @param sceneId
     * @return
     */
    List<Door> listByWaitPoint(long waitPointId, long storeId,Long sceneId);

    int delete(Door door);
}