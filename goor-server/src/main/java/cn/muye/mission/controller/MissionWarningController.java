package cn.muye.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.mission.MissionWarning;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.controller.BaseController;
import cn.muye.mission.service.MissionWarningService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Selim on 2017/10/23.
 */
@RestController
@RequestMapping(value = "missionWarning")
public class MissionWarningController extends BaseController {

    @Autowired
    private MissionWarningService missionWarningService;


    @RequestMapping(value = "test", method = RequestMethod.POST)
    private AjaxResult test(){
        try {
            Long time = missionWarningService.getWarningTime(30L,24L);
            return AjaxResult.success(time,"test成功");
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return AjaxResult.failed("修改警报提示失败");
        }

    }

}
