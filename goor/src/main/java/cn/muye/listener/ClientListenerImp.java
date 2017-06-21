package cn.muye.listener;

import cn.muye.service.imp.ReceiveServiceImp;
import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import org.apache.log4j.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by enva on 2017/5/4.
 */
public class ClientListenerImp implements ClientListener {
        private static Logger logger = Logger.getLogger(ClientListenerImp.class);

        private final ScheduledExecutorService scheduledExecutor;
        boolean flag = true;
        private final Lock lockHeartbeat = new ReentrantLock();

        public ClientListenerImp(ScheduledExecutorService scheduledExecutor) {
                this.scheduledExecutor = scheduledExecutor;
        }

        @Override
        public void onConnected(Client client) {
                flag = true;
        }

        @Override
        public void onDisConnected(Client client) {
                flag = false;
        }

        @Override
        public void onHandshakeOk(final Client client, final int heartbeat) {
                scheduledExecutor.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                                try{
                                        ClientListenerImp.this.sendHeartbeatWithLock(client, heartbeat);
                                }catch (Exception e){
                                        logger.error("schedule sendHeartbeat exception", e);
                                }
                        }
                }, 10, 10, TimeUnit.SECONDS);
        }

        public void sendHeartbeatWithLock(Client client, int heartbeat) {
                if (this.lockHeartbeat.tryLock()) {
                        try {
                                boolean check = client.healthCheck();
                                boolean isRunning = client.isRunning();
                                if (!check || !isRunning) {
                                        client.start();
                                        logger.info("fastConnect============");
                                }
//                                logger.info("send heartBeet============"+heartbeat+",healthCheck=" + check+",isRunning=" + isRunning);
                        } catch (final Exception e) {
                                logger.error("sendHeartbeat exception", e);
                        } finally {
                                this.lockHeartbeat.unlock();
                        }
                } else {
                        logger.warn("lock heartBeat, but failed.");
                }
        }

        @Override
        public void onReceivePush(Client client, byte[] content, int messageId) {
                if (messageId > 0) {
                        client.ack(messageId);
                }
                new ReceiveServiceImp(client, content);
        }

        @Override
        public void onKickUser(String deviceId, String userId) {
                System.out.println("deviceId1========"+deviceId+"userId========="+userId);
        }

        @Override
        public void onBind(boolean success, String userId) {
                System.out.println("deviceId2========"+success+"userId========="+userId);
        }

        @Override
        public void onUnbind(boolean success, String userId) {
                System.out.println("deviceId3========"+success+"userId========="+userId);
        }

}
