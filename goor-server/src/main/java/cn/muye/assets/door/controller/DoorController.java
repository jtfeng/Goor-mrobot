package cn.muye.assets.door.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.door.Door;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.door.service.DoorService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.SessionUtil;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class DoorController {

    @Autowired
    private DoorService doorService;
    @Autowired
    private PointService pointService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DoorController.class);

    /**
     * 分页查询门
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = {"assets/door"}, method = RequestMethod.GET)
    @ResponseBody
    private AjaxResult pageStation(WhereRequest whereRequest) {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if(scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            //TODO 从session取切换门店的ID，现在先写死
            List<Door> doorList = doorService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID,scene.getId());

            PageInfo<Door> pageList = new PageInfo<Door>(doorList);
            return AjaxResult.success(pageList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统内部查询出错");
        }
    }

    /**
     * 删除单个门接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "assets/door/{id}", method = RequestMethod.DELETE)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult deleteMapPoint(@PathVariable long id) throws Exception {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if(scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            //TODO 从session取切换门店的ID，现在先写死
            Door doorDB = doorService.findById(id, SearchConstants.FAKE_MERCHANT_STORE_ID,scene.getId());
            if (doorDB == null) {
                return AjaxResult.failed("删除对象不存在");
            }
            doorDB.setActive(Constant.DELETE);
//            stationService.delete(stationDB);
            doorService.update(doorDB);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("被使用，无法删除");
        }
    }

    /**
     * 新增或修改门
     *
     * @param door
     * @return
     */
    @RequestMapping(value = {"assets/door"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改门", httpMethod = "POST", notes = "新增或修改门")
    @ResponseBody
    public AjaxResult saveOrUpdateStation(@ApiParam(value = "门") @RequestBody Door door) {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if(scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            Long sceneId = scene.getId();

            String name = door.getName();
            if (StringUtil.isNullOrEmpty(name)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "门名称不能为空");
            }
            //TODO 从session取切换门店的ID，现在先写死
            Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

            //判断是否有重复的名称,且查找到的对象ID不是提交的对象ID
            List<Door> doorDbList = doorService.listByName(name,storeId,sceneId);
            int size = doorDbList.size();
            Long id = door.getId();
            if (doorDbList != null
                    && (size > 1
                    || (size == 1 && id == null)
                    || (size == 1 && id != null && !doorDbList.get(0).getId().equals(id)))) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "门名称重复");
            }

            //校验点都是数据库里的点
            if (!isPointExist(door)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "点参数错误");
            }

            checkWaitPoint(door);

            if (door != null && id != null) { //修改

                Door doorDb = doorService.findById(id, storeId,sceneId);
                if (doorDb == null) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要修改的对象不存在");
                }
                doorDb.setName(door.getName());
                doorDb.setInfo(door.getInfo());
                doorDb.setGoPoint(door.getGoPoint());
                doorDb.setOutPoint(door.getOutPoint());
                doorDb.setWaitPoint(door.getWaitPoint());
                doorDb.setIp(door.getIp());

                doorService.update(doorDb);
                return AjaxResult.success(doorDb, "修改成功");
            } else if (door != null && id == null) { //新增
                door.setActive(Constant.NORMAL);
                door.setSceneId(scene.getId());
                door.setSceneName(scene.getMapSceneName());
                MapPoint waitPoint = pointService.findById(door.getWaitPoint());
                door.setMapName(waitPoint.getMapName());
                //TODO 从切换的门店取门店ID
                door.setStoreId(storeId);
                door.setCreateTime(new Date());
                //TODO 从session取登录用户ID
                door.setCreatedBy(SearchConstants.FAKE_MERCHANT_STORE_ID);

                doorService.save(door);
                return AjaxResult.success(door, "新增成功");
            } else {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("出错");
        }
    }


    /**
     * 查找等待点是否已经是复制的门等待点，如果不是则复制并新增一个
     * @param door
     * @return
     */
    private Door checkWaitPoint(Door door) {
        //查找等待点是否已经是复制的电梯等待点，如果不是则复制并新增一个
        MapPoint oldPoint = pointService.findById(door.getWaitPoint());
        //我们定义站的点明必须包含station,所以未找到的时候，就新建一个
        if(oldPoint.getPointAlias().indexOf(Constant.DOOR_WAIT) <= -1
                && oldPoint.getCloudMapPointTypeId() != MapPointType.DOOR_WAIT.getCaption()) {
            MapPoint newPoint = new MapPoint();
            MapPoint.copyValue(newPoint, oldPoint);
            newPoint.setPointAlias(newPoint.getPointName()+ "_" +Constant.DOOR_WAIT + "_" + door.getName());
            newPoint.setId(null);
            newPoint.setCloudMapPointTypeId(MapPointType.DOOR_WAIT.getCaption());
            pointService.save(newPoint);
            door.setWaitPoint(newPoint.getId());
        }
        return door;
    }

    /**
     * 校验door绑定的点是否都存在，且三个点必须在同一个地图
     *
     * @param door
     * @return
     */
    private boolean isPointExist(Door door) {
        MapPoint goPointDB = null;
        if(door.getGoPoint() != null) {
            goPointDB = pointService.findById(door.getGoPoint());
            if (goPointDB == null) {
                return false;
            }
        }
        MapPoint outPointDB = null;
        if(door.getOutPoint() != null) {
            outPointDB = pointService.findById(door.getOutPoint());
            if (outPointDB == null) {
                return false;
            }
            //校验out点和go点是不是在同一个地图
            if(outPointDB.getMapName() == null || !outPointDB.getMapName().equals(goPointDB.getMapName()) ) {
                return false;
            }
        }
        MapPoint waitPointDB = null;
        if(door.getWaitPoint() != null) {
            waitPointDB = pointService.findById(door.getWaitPoint());
            if (waitPointDB == null) {
                return false;
            }
            //校验out点和wait点是不是在同一个地图
            if(waitPointDB.getMapName() == null || !waitPointDB.getMapName().equals(outPointDB.getMapName()) ) {
                return false;
            }
        }

        return true;
    }
}