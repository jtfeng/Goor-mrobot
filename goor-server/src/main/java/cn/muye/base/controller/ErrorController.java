package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorController extends BasicErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

    @Autowired
    public ErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        //输出自定义的Json格式
        Map<String, Object> map = new HashMap<>();
        LOGGER.error(body.toString());
        int errorCode = (int) body.get("status");
        if (errorCode == Constant.ERROR_CODE_NOT_LOGGED) {
            map.put("message", "用户未登录");
            map.put("code", Constant.ERROR_CODE_NOT_LOGGED);
            map.put("data", "");
            map.put("success", false);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else if (errorCode == Constant.ERROR_CODE_NOT_AUTHORIZED) {
            map.put("message", "用户无权限");
            map.put("code", Constant.ERROR_CODE_NOT_AUTHORIZED);
            map.put("data", "");
            map.put("success", false);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("message", "系统内部错误");
            map.put("code", AjaxResult.CODE_SYSTEM_ERROR);
            map.put("data", "");
            map.put("success", false);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * 覆盖默认的HTML响应
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));

        //指定自定义的视图
        return new ModelAndView("system/404", model);
    }
}