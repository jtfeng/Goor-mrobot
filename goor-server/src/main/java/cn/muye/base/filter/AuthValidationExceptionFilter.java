package cn.muye.base.filter;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class avoid to throw full stack trace to client.
 * The exception is logged in the server log.
 *
 * @author ccoman
 *         TODO should this class extend something else that ExceptionTranslationFilter?
 */
@Service
public class AuthValidationExceptionFilter implements Filter {

    private String[] excludedPageArray;
    private String excludedPages;

    private String authUserUri;

    private static final int LOGGED_IN = 1; //已登录
    private static final int LOGGED_OUT = 0; //未登录

    protected static final Log LOGGER = LogFactory.getLog(AuthValidationExceptionFilter.class);

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        boolean isExcludedPage = true;
        for (String page : excludedPageArray) { //判断是否在过滤url之外
            //删除URL末尾的 /
            if (page.endsWith("/")) {
                page = page.substring(0, page.length() - 1);
            }
            Pattern pattern = Pattern.compile(page);
            String servletPath = ((HttpServletRequest) req).getServletPath();
            Matcher matcher = pattern.matcher(servletPath);
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
                String accessTokenFromReq = httpServletRequest.getHeader("Authorization");
//                String accessTokenFromReq = httpServletRequest.getParameter("access_token");
                String result = HttpClientUtil.executeGet(null, accessTokenFromReq, authUserUri, null, null, "UTF-8", true);
                JSONObject jsonObject = JSON.parseObject(result);
                String principal = jsonObject.getString("principal");
                if (StringUtil.isNullOrEmpty(principal)) {
                    response.setStatus(Constant.ERROR_CODE_NOT_LOGGED);
                    response.sendError(Constant.ERROR_CODE_NOT_LOGGED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_base_filter_AuthValidationExceptionFilter_java_NMYDLQDL"));
                } else {
                    String userName = JSON.parseObject(principal).getString("username");
                    LOGGER.info("userName===>" + userName);
                    if (CacheInfoManager.getUserLoginStatusCache(userName) != null) {
                        LOGGER.info("CacheInfoManager.getUserLoginStatusCache(" + userName + ")====>" + CacheInfoManager.getUserLoginStatusCache(userName));
                        chain.doFilter(req, res);
                    } else {
                        response.setStatus(Constant.ERROR_CODE_NOT_LOGGED);
                        response.sendError(Constant.ERROR_CODE_NOT_LOGGED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_base_filter_AuthValidationExceptionFilter_java_NMYDLQDL"));
                    }
                }
            } catch (Exception e) {
                if (res instanceof HttpServletResponse) {
                    response.setStatus(((HttpServletResponse) res).getStatus());
                    response.sendError(((HttpServletResponse) res).getStatus(), "An expected error occured!");
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
        authUserUri = fConfig.getInitParameter("authUserUri");
        if (!StringUtil.isNullOrEmpty(excludedPages)) {
            excludedPageArray = excludedPages.split(",");
        }
        return;
    }

}
