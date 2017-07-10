package cn.muye.tcpser.elevator;

import cn.mrobot.utils.HexStringUtil;
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

    @Async("elevatorTcpSerClientAsync")
    public Future<String> taskElevatorTcpSerClient(Socket client) throws InterruptedException{
        logger.info("taskElevatorTcpSerClient. One Client connected...");
        InputStream inputStream;
        try {
            inputStream = client.getInputStream();
        } catch (IOException e) {
            logger.info("taskElevatorTcpSerClient error...:" + e.getMessage());
            return new AsyncResult<>("taskElevatorTcpSerClient accomplished and exit!");
        }

        StringBuilder s = new StringBuilder();

        //这里处理客户端连接服务器
        try {
            byte buf[] = new byte[50];
            int len = 0;
            while((len=(inputStream.read(buf)))>0){
                byte temp[] = new byte[len];
                System.arraycopy(buf, 0, temp, 0, len);
                s.append(HexStringUtil.bytesToHexString(temp));
            }
            //打印客户端的消息
            logger.info("taskElevatorTcpSerClient get message...:" + s.toString());
            //处理消息，并发送通知

        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info("taskElevatorTcpSerClient error...:" + e.getMessage());
        }

        logger.info("taskElevatorTcpSerClient closed...");

        return new AsyncResult<>("taskElevatorTcpSerClient accomplished and exit!");
    }

}
