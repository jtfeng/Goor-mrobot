package cn.muye;

import cn.muye.base.filter.AuthValidationExceptionFilter;
import cn.muye.base.listener.ClientListenerImp;
import cn.muye.base.service.batch.ScheduledHandle;
import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import com.mpush.client.ClientConfig;
import com.mpush.util.DefaultLogger;
import org.apache.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@MapperScan("cn.muye.**.mapper")
@EnableTransactionManagement
@EnableResourceServer
@ComponentScan(basePackages ="com.mrobot")
public class Application {
    private static Logger logger = Logger.getLogger(Application.class);

    @Value("${mpush.publicKey}")
    private String publicKey;

    @Value("${mpush.allocServer}")
    private String allocServer;

    @Value("${ros.path}")
    private String rosPath;

    @Value("${mpush.deviceId}")
    private String deviceId;

    @Value("${mpush.osName}")
    private String osName;

    @Value("${mpush.osVersion}")
    private String osVersion;

    @Value("${mpush.clientVersion}")
    private String clientVersion;

    @Value("${mpush.userId}")
    private String userId;

    @Value("${mpush.tags}")
    private String tags;

    @Value("${mpush.sessionStorageDir}")
    private String sessionStorageDir;


    @Bean
    public Client getClient() {
        int sleep = 1000;

        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Application ClientListenerImp");
            }
        });
        ClientListener listener = new ClientListenerImp(scheduledExecutor);
        Client client = null;
        String cacheDir = Application.class.getResource("/").getFile();
            client = ClientConfig
                    .build()
                    .setPublicKey(publicKey)
                    .setAllotServer(allocServer)
//                    .setServerHost(allocServer)
//                    .setServerPort(3000)
                    .setDeviceId(deviceId)
                    .setOsName(osName)
                    .setOsVersion(osVersion)
                    .setClientVersion(clientVersion)
                    .setUserId(userId)
                    .setTags(tags)
                    .setSessionStorageDir(cacheDir+sessionStorageDir)
                    .setLogger(new DefaultLogger())
                    .setLogEnabled(true)
                    .setEnableHttpProxy(true)
                    .setClientListener(listener)
                    .create();
            client.start();
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                logger.error("Application client start sleep error=========",e);
            }

        return client;
    }

    @Bean
    public ScheduledExecutorService scheduledHandle(){
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Application scheduledHandle");
            }
        });
        new ScheduledHandle(scheduledExecutor);
        return scheduledExecutor;
    }

    /**
     * 配置认证错误过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean myFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthValidationExceptionFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("excludedUrl", "/account/user/logOut,/account/user/login/pad,/account/user/login");
        registration.setName("authValidationExceptionFilter");
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

    /**
     * Start
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("SpringBoot Start Success");
    }

}
