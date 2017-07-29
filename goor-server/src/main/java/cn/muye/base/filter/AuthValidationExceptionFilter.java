package cn.muye.base.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.StringUtil;
import cn.muye.account.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * This class avoid to throw full stack trace to client.
 * The exception is logged in the server log.
 *
 * @author ccoman
 *         TODO should this class extend something else that ExceptionTranslationFilter?
 */
@Service
public class AuthValidationExceptionFilter implements Filter,ApplicationContextAware {

    private String[] excludedPageArray;
    private String excludedPages;

    private static ApplicationContext applicationContext;

    private UserService userService;

    protected static final Log LOGGER = LogFactory.getLog(AuthValidationExceptionFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        userService = applicationContext.getBean(UserService.class);

        boolean isExcludedPage = true;
        for (String page : excludedPageArray) {//判断是否在过滤url之外
            Pattern pattern = Pattern.compile(page);
            Matcher matcher = pattern.matcher(((HttpServletRequest) req).getServletPath());
            boolean rs = matcher.find();
            if (rs) {
                isExcludedPage = false;
                break;
            }
        }
        if (isExcludedPage) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            try {
                HttpSession session = httpServletRequest.getSession();
                String accessTokenFromReq = httpServletRequest.getParameter("access_token");
                String accessToken = (String) session.getAttribute("access_token");
                if (accessToken != null) {
                    if (!accessToken.equals(accessTokenFromReq)) { //说明已登录但请求的access_token有误，无权限
                        response.setStatus(Constant.ERROR_CODE_NOT_AUTHORIZED);
                        response.sendError(Constant.ERROR_CODE_NOT_AUTHORIZED, "您无权限");
                    } else if (accessToken.equals(accessTokenFromReq)) {
                        chain.doFilter(req, res);
                    }
                } else {
                    response.setStatus(Constant.ERROR_CODE_NOT_LOGGED);
                    response.sendError(Constant.ERROR_CODE_NOT_LOGGED, "您没有登录，请登录");
                }
            } catch (Exception e) {
                if (res instanceof HttpServletResponse) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An expected error occured!");
                }
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 初始化函数，获取需要排除在外的url
     */
    public void init(FilterConfig fConfig) throws ServletException {
        excludedPages = fConfig.getInitParameter("excludedUrl");
        if (!StringUtil.isNullOrEmpty(excludedPages)) {
            excludedPageArray = excludedPages.split(",");
        }
        return;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AuthValidationExceptionFilter.applicationContext = applicationContext;
    }
}