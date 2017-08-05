package cn.muye.base.service.imp;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);

    private static ApplicationContext applicationContext;

    private OffLineMessageService offLineMessageService;

    private ReceiveMessageService receiveMessageService;

    private RobotService robotService;

    public ScheduledHandleServiceImp() {

    }

    @Override
    public void executeTwentyThreeAtNightPerDay() {
        logger.info("Scheduled clear message start");
        try {
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
            ReceiveMessage receiveMessage = new ReceiveMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            receiveMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }

    @Override
    public void executeRobotHeartBeat() {
        robotService = applicationContext.getBean(RobotService.class);
        //拿sendTime跟内存里的同步时间
        Long currentTime = new Date().getTime();
        List<Robot> list = robotService.listRobot(SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (list != null && list.size() > 0) {
            for (Robot robot : list) {
                String code = robot.getCode();
                Long sendTime = CacheInfoManager.getRobotAutoRegisterTimeCache(code);
                Robot robotDb = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);
                if (robotDb != null) {
                    //如果大于1分钟
                    if (sendTime == null || (currentTime - sendTime > Constant.CHECK_IF_OFFLINE_TIME)) {
                        robotDb.setOnline(false);
                        robotService.updateRobotAndBindChargerMapPoint(robotDb, null, null, null, null, null);
                    } else {
                        robotDb.setOnline(true);
                        robotService.updateRobotAndBindChargerMapPoint(robotDb, null, null, null, null, null);
                    }
                }

            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }
}
