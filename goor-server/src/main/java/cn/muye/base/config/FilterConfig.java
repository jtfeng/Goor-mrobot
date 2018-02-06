package cn.muye.base.config;

import cn.muye.base.filter.AuthValidationExceptionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.HashMap;

/**
 * Created by Ray.Fu on 2017/7/8.
 */
@Configuration
public class FilterConfig {

    @Value("${security.oauth2.resource.user-info-uri}")
    private String authUserUri;

    @Value("${url.excludeUri}")
    private String excludeUri;

    /**
     * 配置认证错误过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean myFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthValidationExceptionFilter());
        registration.addUrlPatterns("/*");
        registration.setName("authValidationExceptionFilter");
        registration.setInitParameters(new HashMap<String, String>() {
            {
                put("authUserUri", authUserUri);
                put("excludedUrl", excludeUri);
            }
        });
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
