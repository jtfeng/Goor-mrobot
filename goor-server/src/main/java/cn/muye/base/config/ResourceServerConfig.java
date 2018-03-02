package cn.muye.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/account/user/login").permitAll()
                .antMatchers("/account/user/login/pad").permitAll()
                .antMatchers("/goor/ws").permitAll()
                .antMatchers("/resources/static/**").permitAll()
                .antMatchers("/services/**").permitAll()
                .antMatchers("/operation/**").permitAll()
                .antMatchers("/appliance/**").permitAll()
                .antMatchers("/check**").permitAll()
                .antMatchers("/druid/**").permitAll()
                .antMatchers("/**").permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);           //自定义异常处理
//        http.authorizeRequests().anyRequest().permitAll();
    }




}
