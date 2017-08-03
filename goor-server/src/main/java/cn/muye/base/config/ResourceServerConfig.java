package cn.muye.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/account/user/login").permitAll()
//                .antMatchers("/account/user/login/pad").permitAll()
//                .antMatchers("/account/user/logOut").permitAll()
//                .antMatchers("/resources/static/**").permitAll()
//                .antMatchers("/services/**").permitAll()
//                .antMatchers("/check**").permitAll()
//                .antMatchers("/druid/**").permitAll()
//                .antMatchers("/**").permitAll()
        http.authorizeRequests().anyRequest().permitAll();
    }

}
