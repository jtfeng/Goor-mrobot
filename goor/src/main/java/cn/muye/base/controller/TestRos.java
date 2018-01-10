package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.VersionConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.TestService;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestRos {
    private Logger logger = Logger.getLogger(TestRos.class);
    @Autowired
    private Ros ros;
    @Autowired
    private ScheduledHandleService scheduledHandleService;


    @RequestMapping(value = "testRos", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult testRos(@RequestParam("aa") String aa) {
        //前面是服务名，后面是服务类型
        TestService setService = new TestService(this.ros, "/rosapi/set_param", "rosapi/SetParam");
        TestService getService = new TestService(this.ros, "/rosapi/get_param", "rosapi/GetParam");
        TestService deleteService = new TestService(this.ros, "/rosapi/delete_param", "rosapi/DeleteParam");
        //这里试了value只能用数字，不能有字母和特殊符号，很奇怪，直接通过rosparam命令是可以设置字符串的
        ServiceRequest request = new ServiceRequest("{\"name\": \"envaTest\", \"value\": \"30\"}");
        ServiceRequest request1 = new ServiceRequest("{\"name\": \"envaTest\"}");
        setService.callService(request, new ServiceCallback() {
            @Override
            public void handleServiceResponse(ServiceResponse response) {
                logger.info("setServicesetServicesetServicesetService ==========: " + response.toString());
                CacheInfoManager.setUUIDHandledCache(aa);
            }
        });
        getService.callService(request1, new ServiceCallback() {
            @Override
            public void handleServiceResponse(ServiceResponse response) {
                logger.info("getServicegetServicegetServicegetService ==========: " + response.toString());
            }
        });
        /*deleteService.callService(request1, new ServiceCallback() {
            @Override
            public void handleServiceResponse(ServiceResponse response) {
                log.info("deleteServicedeleteServicedeleteService ==========: " + response.toString());
            }
        });*/
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("sssssssssssssss============"+CacheInfoManager.getUUIDHandledCache(aa)+"");
//		ServiceResponse response = setService.callServiceAndWait(request);
//		ServiceResponse response1 = getService.callServiceAndWait(request1);
//		ServiceResponse response2 = deleteService.callServiceAndWait(request);
//		log.info(response+"");
//        log.info(response1+"");
//        log.info(response2+"");
//        log.info(response.toString());
//        log.info(response1.toString());
//        log.info(response2.toString());

        try {
//            scheduledHandleService.writeRosParamGoorVersion();
            logger.info("################写入参数服务器agent版本的定时任务");
//            ros = applicationContext.getBean(Ros.class);
            if(ros == null) {
                logger.info("还未连上ros");
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "还未连上ros");
            }
//            TestService getService = new TestService(this.ros, "/rosapi/get_param", "rosapi/GetParam");
//            TestService setService = new TestService(this.ros, "/rosapi/set_param", "rosapi/SetParam");
//            TestService deleteService = new TestService(this.ros, "/rosapi/delete_param", "rosapi/DeleteParam");
            ServiceRequest setRequest = new ServiceRequest("{\"name\": \"" + VersionConstants.VERSION_NOAH_GOOR_KEY + "\", \"value\": \"" + VersionConstants.VERSION_NOAH_GOOR + "\"}");
            ServiceRequest getRequest = new ServiceRequest("{\"name\": \"" + VersionConstants.VERSION_NOAH_GOOR_KEY + "\"}");
            final String[] versionNow = {null};
            getService.callService(getRequest, new ServiceCallback() {
                @Override
                public void handleServiceResponse(ServiceResponse response) {
                    versionNow[0] = response.toString();
                    logger.info("getServicegetServicegetServicegetService当前agent版本参数为 ==========: " + versionNow[0]);
                }
            });

            /*if(VersionConstants.VERSION_NOAH_GOOR.equals(versionNow[0])) {
                logger.info("检测当前agent版本参数与agent实际版本一致。");
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "检测当前agent版本参数与agent实际版本一致。");
            }

            logger.info("检测当前agent版本参数与agent实际版本不一致，开始写入参数服务器。");

            deleteService.callService(getRequest, new ServiceCallback() {
                @Override
                public void handleServiceResponse(ServiceResponse response) {
                    logger.info("deleteServicedeleteServicedeleteService ==========: " + response.toString());
                }
            });*/
            setService.callService(setRequest, new ServiceCallback() {
                @Override
                public void handleServiceResponse(ServiceResponse response) {
                    logger.info("setServicesetServicesetServicesetService ==========: " + response.toString());
                    CacheInfoManager.setUUIDHandledCache("setRosParam");
                }
            });

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
    }
}