package cn.muye.log.charge.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotService;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.muye.log.charge.service.ChargeInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
                return AjaxResult.failed("设备编号不能为空");
            }

            Robot robot = robotService.getByCode(code);
            if (null == robot) {
                return AjaxResult.failed("机器人编号（" + code + "）不存在");
            }
            List<ChargeInfo> chargeInfoList = chargeInfoService.getByDeviceId(code);
            if (chargeInfoList.size() <= 0) {
                return AjaxResult.failed("无当前机器人（" + code + "）信息");
            }

            return AjaxResult.success(toEntity(new HashMap(),chargeInfoList.get(0)));
        } catch (Exception e) {
            LOGGER.error("获取信息出错", e);
            return AjaxResult.failed("系统错误");
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
        List<ChargeInfo> list = chargeInfoService.lists();
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
        return map;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ChargeInfoController.applicationContext = applicationContext;
    }
}
