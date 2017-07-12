package cn.muye.base.controller;

import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.consumer.service.PickUpPswdVerifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by abel on 17-7-12.
 */
@RestController
@RequestMapping("app/func/test")
@Api(
        value = "机器人APP功能接口测试",
        description = "机器人APP功能接口测试")
public class AppFuncTestController {

    @Autowired
    PickUpPswdVerifyService pickUpPswdVerifyService;

    @PostMapping("/pickUpPswdVerify")
    @ApiOperation(
            value = "APP界面输入密码，调用云端验证结果",
            notes = "APP界面输入密码，调用云端验证结果")
    public AjaxResult pickUpPswdVerify(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody PickUpPswdVerifyBean body){
        pickUpPswdVerifyService.sendPickUpPswdVerify(body);
        return AjaxResult.success("调用接口成功");
    }
}
