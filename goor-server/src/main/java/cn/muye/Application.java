package cn.muye;

import cn.muye.base.service.batch.ScheduledHandle;
import org.apache.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@MapperScan("cn.muye.**.mapper")
@EnableTransactionManagement
@EnableResourceServer
public class Application {
    private static Logger logger = Logger.getLogger(Application.class);

    @Bean
    public ScheduledExecutorService scheduledHandle(){
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3, (e) -> new Thread(e, "Application scheduledHandle"));
        new ScheduledHandle(scheduledExecutor);
        return scheduledExecutor;
    }


	/**
     * Start
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("SpringBoot Start Success");
    }

}
