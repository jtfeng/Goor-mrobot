package cn.muye.service.tcpser.elevator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 * Created by abel on 17-7-10.
 */
public class ElevatorAsyncTask {

    protected static final Logger logger = LoggerFactory.getLogger(ElevatorAsyncTask.class);

    static ServerSocket serverSocket = null;
    static int serverPort = 8181;

    static {
        try {
            //这里启动tcp socket服务器
            serverSocket=new ServerSocket(serverPort);
            logger.info("########## taskElevatorTcpSer started. Start the tcp server... port is: " + serverPort);
        } catch (IOException e) {
            logger.info("taskElevatorTcpSer start error...:" + e.getMessage());
        }
    }

    public ElevatorAsyncTask(ElevatorClientAsyncTask elevatorClientAsyncTask) {
        this.elevatorClientAsyncTask = elevatorClientAsyncTask;
    }

    ElevatorClientAsyncTask elevatorClientAsyncTask;

    @Async("elevatorTcpSerAsync")
    public Future<String> taskElevatorTcpSer(HttpServletRequest request) throws InterruptedException{
        try {
            //等待客户端的连接
            while (true){
                Socket client = serverSocket.accept();
                elevatorClientAsyncTask.taskElevatorTcpSerClient(client, request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>("taskElevatorTcpSer accomplished and exit!");
    }


}
