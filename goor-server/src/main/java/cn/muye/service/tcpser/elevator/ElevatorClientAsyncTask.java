package cn.muye.service.tcpser.elevator;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.elevator.LogElevator;
import cn.mrobot.utils.HexStringUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.log.elevator.service.LogElevatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 * Created by abel on 17-7-10.
 */
public class ElevatorClientAsyncTask {

    protected static final Logger logger = LoggerFactory.getLogger(ElevatorClientAsyncTask.class);

    LogElevatorService logElevatorService;

    public ElevatorClientAsyncTask(LogElevatorService taskLogElevatorService) {
        this.logElevatorService = taskLogElevatorService;
    }

    @Async("elevatorTcpSerClientAsync")
    public Future<String> taskElevatorTcpSerClient(Socket client) throws InterruptedException{
        logger.info("Remote Address: " + client.getRemoteSocketAddress() + "taskElevatorTcpSerClient. One Client connected...");
        InputStream inputStream;
        try {
            inputStream = client.getInputStream();
        } catch (IOException e) {
            logger.info("taskElevatorTcpSerClient error...:" + e.getMessage());
            return new AsyncResult<>("taskElevatorTcpSerClient accomplished and exit!");
        }

        //这里处理客户端连接服务器
        try {
            byte buf[] = new byte[8];
            while (!client.isClosed()){
                if (inputStream.available() >= buf.length){
                    //读取
                    inputStream.read(buf, 0, buf.length);
                    //打印客户端的消息
                    if (TopicConstants.DEBUG)
                    logger.info("Remote Address: " + client.getRemoteSocketAddress() + " ,taskElevatorTcpSerClient get message...:" + HexStringUtil.bytesToHexString(buf));
                    //处理消息，并发送通知
                    LogElevator logElevator = new LogElevator();
                    logElevator.setAddr(client.getRemoteSocketAddress().toString());
                    logElevator.setValue(HexStringUtil.bytesToHexString(buf));
                    if (!StringUtil.isEmpty(logElevator.getValue())){
                        logElevator.setValue(logElevator.getValue().toUpperCase());
                    }
                    if (logElevatorService != null){
                        try {
                            logElevatorService.save(logElevator);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.info(e.getMessage());
                        }
                        if (logElevator.getId() != null){
                            if (TopicConstants.DEBUG)
                            logger.info("保存日志成功！");
                        }else{
                            logger.info("保存日志失败！");
                        }
                    }else{
                        logger.info("service 对象为null！");
                    }
                }else{
                    Thread.sleep(100);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info("taskElevatorTcpSerClient error...:" + e.getMessage());
        }

        logger.info("taskElevatorTcpSerClient closed...");

        return new AsyncResult<>("taskElevatorTcpSerClient accomplished and exit!");
    }

}
