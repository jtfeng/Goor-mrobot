package cn.muye.base;

import cn.mrobot.bean.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
public class DefaultController {

    @GetMapping("/403")
    public void error403(HttpServletResponse response) {
        try {
            response.sendError(Constant.ERROR_CODE_NOT_AUTHORIZED, "无权限");
        } catch (IOException e) {
            log.error("403错误处理报错" , e);
        }
    }
}