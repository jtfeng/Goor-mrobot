package cn.muye.erp.setting.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.station.service.StationService;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jelynn
 * @date 2018/1/2
 */
@RestController
public class ErpSystemSettingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErpSystemSettingController.class);

    @Autowired
    private StationService stationService;

    /**
     * 设置不对诺亚开放的手术室
     * 前端将用户选中的所有不开放手术室传输到后台，后台查询出所有手术室列表，将提交的数据中的手术室站置为不对机器人开放，其他的置为对机器人开放
     *
     * @param operations
     * @return
     */
    @RequestMapping(name = "services/operation/setting/notAccessOperation", method = RequestMethod.GET)
    public AjaxResult setNotAccessOperation(@RequestParam("operations") String operations) {
        if (StringUtil.isBlank(operations)) {
            return AjaxResult.failed("参数不能为空");
        }
        LOGGER.info("设置不对诺亚开放的手术室 = " + operations);
        List<Long> stationIdList = JSONArray.parseArray(operations, Long.class);
        //列出所有的手术室站
        List<Station> stationList = stationService.listStationsByStationTypeCode(StationType.OPERATION.getCaption());
        for (Long stationId : stationIdList) {
            Station station = new Station();
            station.setId(stationId);
            station.setRobotAccess(Station.RobotAccess.NOT_ACCESS.getCode());
            stationService.updateSelective(station);
            //若果站列表中包含不开放手术室站，怎么将该站从站列列表中移除
            if (stationList.contains(station)) {
                stationList.remove(station);
            }
        }
        //将站列列表中剩余的站改为机器人可达
        for (Station station : stationList) {
            station.setRobotAccess(Station.RobotAccess.ACCESS.getCode());
            stationService.updateSelective(station);
        }
        return AjaxResult.success("操作成功");
    }
}
