package cn.muye.base.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.HttpClientUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.account.user.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class AuthValidationExceptionFilter implements Filter{

    private String[] excludedPageArray;
    private String excludedPages;

    private String authUserUri;

    protected static final Log LOGGER = LogFactory.getLog(AuthValidationExceptionFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
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
                String accessTokenFromReq = httpServletRequest.getParameter("access_token");
                String result = HttpClientUtil.executeGet(null,authUserUri+"?access_token="+accessTokenFromReq , null, null, "UTF-8", true);
                JSONObject jsonObject = JSON.parseObject(result);
                String principal = jsonObject.getString("principal");
                if (StringUtil.isNullOrEmpty(principal)) {
                    response.setStatus(Constant.ERROR_CODE_NOT_LOGGED);
                    response.sendError(Constant.ERROR_CODE_NOT_LOGGED, "您没有登录，请登录");
                } else {
                    chain.doFilter(req, res);
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
        authUserUri = fConfig.getInitParameter("authUserUri");
        if (!StringUtil.isNullOrEmpty(excludedPages)) {
            excludedPageArray = excludedPages.split(",");
        }
        return;
    }

}