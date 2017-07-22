package cn.muye.base.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.dto.auth.UserAuth;
import cn.mrobot.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class avoid to throw full stack trace to client.
 * The exception is logged in the server log.
 *
 * @author ccoman
 *         TODO should this class extend something else that ExceptionTranslationFilter?
 */
public class AuthValidationExceptionFilter implements Filter {

    private String[] excludedPageArray;
    private String excludedPages;

    protected static final Logger LOGGER = LoggerFactory.getLogger(AuthValidationExceptionFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        boolean isExcludedPage = true;
        for (String page : excludedPageArray) {//判断是否在过滤url之外
            if (((HttpServletRequest) req).getServletPath().equals(page)) {
                isExcludedPage = false;
                break;
            }
        }
        if (isExcludedPage) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            try {
                String accessTokenFromReq = httpServletRequest.getParameter("access_token");
//                Object principalObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//                String accessToken = CacheInfoManager.getUserAccessTokenCache(principalObject.toString());
                String accessTokenStr = (String)httpServletRequest.getSession().getAttribute("access_token");
                UserAuth userAuth = JSON.parseObject(accessTokenStr, UserAuth.class);
                if (userAuth != null) {
                    String accessToken = userAuth.getAccessToken();
                    if (!StringUtil.isNullOrEmpty(accessToken)) {
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
}