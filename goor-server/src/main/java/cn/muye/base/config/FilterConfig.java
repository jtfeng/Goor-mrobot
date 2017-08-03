package cn.muye.base.config;

//import cn.muye.base.filter.AuthValidationExceptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by Ray.Fu on 2017/7/8.
 */
@Configuration
public class FilterConfig {

    /**
     * 配置认证错误过滤器
     * @return
     */
//    @Bean
//    public FilterRegistrationBean myFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new AuthValidationExceptionFilter());
//        registration.addUrlPatterns("/*");
//        registration.addInitParameter("excludedUrl",
//                "/account/user/logOut," +
//                "/account/user/login/pad," +
//                "/account/user/login," +
//                "/services/,/check*");
//        registration.setName("authValidationExceptionFilter");
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
//        return registration;
//    }

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
