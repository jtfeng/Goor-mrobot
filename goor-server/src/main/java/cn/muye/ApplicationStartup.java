package cn.muye;

import cn.muye.service.tcpser.elevator.ElevatorAsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by abel on 17-7-10.
 */
@Configuration
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    protected static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Autowired
    ElevatorAsyncTask elevatorAsyncTask;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //启动电梯tcp server
        try {
            elevatorAsyncTask.taskElevatorTcpSer(null);
            logger.info("ApplicationStartup ########### start elevator ser successed!");
        } catch (InterruptedException e) {
            logger.info("ApplicationStartup ########### start elevator ser failed!");
        }
    }
}
