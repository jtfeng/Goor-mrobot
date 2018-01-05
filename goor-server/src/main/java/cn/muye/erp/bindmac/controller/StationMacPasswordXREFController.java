package cn.muye.erp.bindmac.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.StationService;
import cn.muye.erp.bindmac.service.StationMacPasswordXREFService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
@RestController
public class StationMacPasswordXREFController {

    @Autowired
    private StationMacPasswordXREFService stationMacPasswordXREFService;

    @Autowired
    private StationService stationService;

    /**
     * 保存
     *
     * @param stationMacPasswordXREF
     * @return
     */
    @RequestMapping(value = "services/operation/mac/bind", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody StationMacPasswordXREF stationMacPasswordXREF) {
        //如果绑定关系为无菌器械室，则查询出无菌器械室站点，关联信息，因为设置无菌器械室站点时没有站选择界面
        if (StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM.getCode() == stationMacPasswordXREF.getType()){
            List<Station> stationList = stationService.listStationsByStationTypeCode(StationType.ASEPTIC_APPARATUS_ROOM.getCaption());
            if (null == stationList || stationList.size() <= 0){
                return AjaxResult.failed("系统未配置无菌器械室站点");
            }
            stationMacPasswordXREF.setStation(stationList.get(0));
        }
        return saveOrUpdate(stationMacPasswordXREF);
    }

    /**
     * 更新，根据mac更新
     *
     * @param stationMacPasswordXREF
     * @return
     */
    @RequestMapping(value = "services/operation/mac/bind", method = RequestMethod.PUT)
    public AjaxResult update(@RequestBody StationMacPasswordXREF stationMacPasswordXREF) {
        return saveOrUpdate(stationMacPasswordXREF);
    }

    public AjaxResult saveOrUpdate(StationMacPasswordXREF operaXREF) {
        if (StringUtil.isBlank(operaXREF.getMac()) ||
                operaXREF.getStation().getId() == null ||
                operaXREF.getType() == 0) {
            return AjaxResult.failed("站Id，电脑MAC不能为空");
        }
        //如果是无菌器械室绑定，则需要移除原无菌器械室的绑定关系
        if (operaXREF.getType() == StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM.getCode()) {
            stationMacPasswordXREFService.deleteByType(StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM);
        }
        StationMacPasswordXREF operaXREFDB = stationMacPasswordXREFService.findByMac(operaXREF.getMac());
        operaXREF.init();
        if (operaXREFDB != null) {
            //更新该mac绑定的手术室
            stationMacPasswordXREFService.updateByMac(operaXREF);
        } else {
            //新增mac和手术室的绑定关系
            stationMacPasswordXREFService.saveStationMacPasswordXREF(operaXREF);
        }
        return AjaxResult.success(operaXREF, "绑定成功");
    }

    /**
     * 根据MAC查询
     *
     * @param mac
     * @return
     */
    @RequestMapping(value = "services/operation/mac/bind/query", method = RequestMethod.GET)
    public AjaxResult query(@RequestParam("mac") String mac) {
        if (StringUtil.isBlank(mac)) {
            return AjaxResult.failed("平板MAC不能为空");
        }
        StationMacPasswordXREF stationMacPasswordXREF = stationMacPasswordXREFService.findByMac(mac);
        if (null == stationMacPasswordXREF) {
            return AjaxResult.failed("未查询到绑定信息");
        }
        return AjaxResult.success(stationMacPasswordXREF, "查询成功");
    }

    /**
     * 列表
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "operation/mac/bind/list", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {

        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<StationMacPasswordXREF> stationMacPasswordXREFList = stationMacPasswordXREFService.list(whereRequest);
        PageInfo<StationMacPasswordXREF> page = new PageInfo<StationMacPasswordXREF>(stationMacPasswordXREFList);
        return AjaxResult.success(page, "查询成功");
    }
}
