package cn.muye.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.mission.MissionWarning;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.controller.BaseController;
import cn.muye.mission.bean.RobotPositionRecord;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.util.PathUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Selim on 2017/10/23.
 */
@RestController
@RequestMapping(value = "missionWarning")
public class MissionWarningController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionWarningController.class);
    @Autowired
    private MissionWarningService missionWarningService;


    @RequestMapping(value = "test", method = RequestMethod.POST)
    private AjaxResult test(){
        try {
            missionWarningService.checkRobotWarningState();
            return AjaxResult.success("test成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("test失败");
        }

    }

    @RequestMapping(value = "addTest", method = RequestMethod.POST)
    private AjaxResult addTest(String robotCode, @RequestBody MapPoint mapPoint){
        try {
            LinkedList<RobotPositionRecord> robotPositionRecordList = CacheInfoManager.getRobotPositionRecordsCache(robotCode);
            //存在位置往内部添加坐标记录
            if(robotPositionRecordList == null){
                //不存在此记录
                LinkedList<RobotPositionRecord> newRobotPositionRecordList = Lists.newLinkedList();
                newRobotPositionRecordList.addLast(new RobotPositionRecord(mapPoint));
                CacheInfoManager.setRobotPositionRecordsCache(robotCode, newRobotPositionRecordList);
            }else {
                //最多记录5个, 去除第一个
                if(robotPositionRecordList.size() == 5){
                    robotPositionRecordList.removeFirst();
                }
                robotPositionRecordList.addLast(new RobotPositionRecord(mapPoint));
            }
            return AjaxResult.success("test成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("test失败");
        }

    }

    @RequestMapping(value = "getTest", method = RequestMethod.GET)
    private AjaxResult getTest(String robotCode){
        try {
            LinkedList<RobotPositionRecord> list = CacheInfoManager.getRobotPositionRecordsCache(robotCode);
            return AjaxResult.success(list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("test失败");
        }

    }


    @RequestMapping(value = "distanceTest", method = RequestMethod.GET)
    private AjaxResult distanceTest(String robotCode){
        try {
            LinkedList<RobotPositionRecord> list = CacheInfoManager.getRobotPositionRecordsCache(robotCode);
            //获取5坐标差值总和
            MapPoint lastMapPoint = null;
            long totalDistance = 0;
            if(list.size() == 5){
                for (RobotPositionRecord robotPositionRecord : list) {
                    MapPoint positionRecord = robotPositionRecord.getRecordPosition();
                    if(lastMapPoint == null){
                        lastMapPoint = positionRecord;
                    }else {
                        totalDistance += PathUtil.calDistance(positionRecord, lastMapPoint);;
                    }
                }
            }
            return AjaxResult.success(totalDistance);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("test失败");
        }

    }



    /**
     * 分页查询
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    private AjaxResult pageListMissionWarnings(WhereRequest whereRequest){
        try {
            List<MissionWarning> missionWarningList =  missionWarningService.pageListMissionWarnings(whereRequest.getPage(),whereRequest.getPageSize());
            PageInfo<MissionWarning> pageInfo = new PageInfo<>(missionWarningList);
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("查询超时警报时间错误");
        }

    }

    /**
     * 新增个警告提示
     * @param missionWarning
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    private AjaxResult saveMissionWarning(@RequestBody MissionWarning missionWarning){
        try {
            boolean hasExist = missionWarningService.hasExistWarning(missionWarning);
            if (hasExist){
                return AjaxResult.failed("该起始点到结束点的记录已存在，无法添加");
            }
            missionWarningService.save(missionWarning);
            return AjaxResult.success("新增警报提示成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("新增警报提示失败");
        }

    }


    /**
     * 修改个警告提示
     * @param missionWarning
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    private AjaxResult updateMissionWarning(@RequestBody MissionWarning missionWarning){
        try {
            missionWarningService.update(missionWarning);
            return AjaxResult.success("修改警报提示成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("修改警报提示失败");
        }

    }

}
