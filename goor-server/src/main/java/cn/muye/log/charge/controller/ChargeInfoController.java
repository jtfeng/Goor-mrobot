package cn.muye.log.charge.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotService;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.charge.service.ChargeInfoService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/1
 * Time: 16:45
 * Describe:
 * Version:1.0
 */
@Controller
public class ChargeInfoController implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static Logger LOGGER = Logger.getLogger(ChargeInfoController.class);

    @Autowired
    private ChargeInfoService chargeInfoService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    /**
     * 实时获取电量信息
     *
     * @return
     */
    @RequestMapping(value = "charge/status", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult ChargeStatus(@RequestParam("code") String code) {
        try {
            if (StringUtil.isNullOrEmpty(code)) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_SBBHBNWK"));
            }

            Robot robot = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (null == robot) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_JQRBH") + code + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_BCZ"));
            }
            List<ChargeInfo> chargeInfoList = chargeInfoService.getByDeviceId(code);
            if (chargeInfoList.size() <= 0) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_WDQJQR") + code + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_XX"));
            }

            return AjaxResult.success(toEntity(new HashMap(),chargeInfoList.get(0)));
        } catch (Exception e) {
            LOGGER.error("获取信息出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_charge_controller_ChargeInfoController_java_XTCW"));
        }
    }

    /**
     * 实时获取电量信息
     *
     * @return
     */
    @RequestMapping(value = "charge/lists", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult lists() {
        //TODO
        ChargeInfoService chargeInfoService = applicationContext.getBean(ChargeInfoService.class);
        List<ChargeInfo> list = chargeInfoService.listAll();
        return AjaxResult.success();
    }


    /**
     * 转化成前端entity
     * append
     * @param map
     * @return
     */
    public Map toEntity(Map map, ChargeInfo chargeInfo) {
        map.put("deviceId", chargeInfo.getDeviceId());
        map.put("chargingStatus", chargeInfo.getChargingStatus());
        map.put("pluginStatus", chargeInfo.getPluginStatus());
        map.put("powerPercent", chargeInfo.getPowerPercent());
        map.put("storeId", chargeInfo.getStoreId());
        map.put("autoCharging", chargeInfo.getAutoCharging());
        return map;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ChargeInfoController.applicationContext = applicationContext;
    }
}
