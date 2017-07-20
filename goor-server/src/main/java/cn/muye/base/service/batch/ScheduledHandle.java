package cn.muye.base.service.batch;

import cn.mrobot.utils.TimeUtil;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import org.apache.log4j.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledHandle {
    private static Logger logger = Logger.getLogger(ScheduledHandle.class);

    private final ScheduledExecutorService scheduledExecutor;

    public ScheduledHandle(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        this.executeTwentyThreeAtNightPerDay();
        this.executeRobotHeartBeat();
    }

    /**
     * 每天晚上23点执行一次
     * 每天定时安排任务进行执行
     */
    public void executeTwentyThreeAtNightPerDay() {
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = TimeUtil.getTimeMillis("23:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    ScheduledHandleService service = new ScheduledHandleServiceImp();
                    service.executeTwentyThreeAtNightPerDay();
                    logger.info("schedule sendMessageWithLock");
                } catch (Exception e) {
                    logger.error("schedule sendMessageWithLock exception", e);
                }
            }
        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    /**
     * 判断是否机器人在线
     */
    public void executeRobotHeartBeat() {
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    ScheduledHandleService service = new ScheduledHandleServiceImp();
                    service.executeRobotHeartBeat();
                } catch (Exception e) {
                    logger.error("schedule publishRosScheduled exception", e);
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

}
