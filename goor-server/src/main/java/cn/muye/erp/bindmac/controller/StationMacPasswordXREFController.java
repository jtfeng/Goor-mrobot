package cn.muye.erp.bindmac.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.utils.AddressUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.StationService;
import cn.muye.erp.bindmac.service.StationMacPasswordXREFService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
@RestController
public class StationMacPasswordXREFController {

    private static final Logger logger = LoggerFactory.getLogger(StationMacPasswordXREFController.class);

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
    public AjaxResult save(@RequestBody StationMacPasswordXREF stationMacPasswordXREF, HttpServletRequest request) {
        //如果绑定关系为无菌器械室，则查询出无菌器械室站点，取出第一个进行关联，因为设置无菌器械室站点时没有站选择界面
        if (StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM.getCode() == stationMacPasswordXREF.getType()) {
            List<Station> stationList = stationService.listStationsByStationTypeCode(StationType.ASEPTIC_APPARATUS_ROOM.getCaption());
            if (null == stationList || stationList.size() <= 0) {
                return AjaxResult.failed("系统未配置无菌器械室站点");
            }
            stationMacPasswordXREF.setStation(stationList.get(0));
        }
        return saveOrUpdate(stationMacPasswordXREF, request);
    }

    public AjaxResult saveOrUpdate(StationMacPasswordXREF operaXREF, HttpServletRequest request) {
        if (operaXREF.getStation().getId() == null || operaXREF.getType() == 0) {
            return AjaxResult.failed("站Id不能为空");
        }
        try {
            //注释掉，不能获取二级路由下的客户端的mac地址
//            String mac = getClientMAC(request);
            String mac = operaXREF.getMac();
            logger.info("mac地址为：" + mac);
            if (StringUtil.isBlank(mac)) {
                return AjaxResult.failed("MAC不能为空");
            }
            operaXREF.setMac(mac);
            //如果是除原无无菌器械室绑定，则需要移菌器械室的绑定关系
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
            StationMacPasswordXREF returnResult = stationMacPasswordXREFService.findXREFById(operaXREF.getId());
            return AjaxResult.success(returnResult, "绑定成功");
        } catch (Exception e) {
            logger.error("保存或更新绑定关系出错", e);
        }
        return AjaxResult.failed("操作失败");
    }

    /**
     * 根据MAC查询
     *
     * @return
     */
    @RequestMapping(value = "services/operation/mac/bind", method = RequestMethod.GET)
    public AjaxResult query(@RequestParam("mac") String mac) {
        try {
            //注释掉，不能获取二级路由下的客户端的mac地址
//            String mac = getClientMAC(request);
            logger.info("mac地址为：" + mac);
            if (StringUtil.isBlank(mac)) {
                return AjaxResult.failed("MAC不能为空");
            }
            StationMacPasswordXREF stationMacPasswordXREF = stationMacPasswordXREFService.findByMac(mac);
            if (null == stationMacPasswordXREF) {
                return AjaxResult.failed("未查询到绑定信息");
            }
            return AjaxResult.success(stationMacPasswordXREF, "查询成功");
        } catch (Exception e) {
            logger.error("根据MAC查询错误", e);
        }
        return AjaxResult.failed("操作失败");
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

    private String getClientMAC(HttpServletRequest request) throws Exception {
        logger.info("获取客户端mac地址");
        String ip = AddressUtils.getIp(request);
        logger.info("获取客户端mac地址，客户端IP地址=" + ip);
        return AddressUtils.getMac(ip);
    }
}
