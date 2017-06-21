/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.mpush.client;


import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import com.mpush.api.http.HttpRequest;
import com.mpush.api.http.HttpResponse;
import com.mpush.api.push.PushContext;
import com.mpush.util.DefaultLogger;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ohun on 2016/1/25.
 *
 * @author ohun@live.cn (夜色)
 */
public class MPushClientTest {
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
    private static final String allocServer = "http://42.159.117.106:9999/";

    public static void main(String[] args) throws Exception {
        int count = 1;
//        String serverHost = "172.16.1.24";
        String serverHost = "42.159.117.106";
        int sleep = 1000;

        if (args != null && args.length > 0) {
            count = Integer.parseInt(args[0]);
            if (args.length > 1) {
                serverHost = args[1];
            }
            if (args.length > 2) {
                sleep = Integer.parseInt(args[1]);
            }
        }

        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        ClientListener listener = new L(scheduledExecutor);
        Client client = null;
        String cacheDir = MPushClientTest.class.getResource("/").getFile();
        System.out.println("cacheDircacheDircacheDir=========="+cacheDir);
        for (int i = 0; i < count; i++) {
            client = ClientConfig
                    .build()
                    .setPublicKey(publicKey)
                    .setAllotServer(allocServer)
//                    .setServerHost(serverHost)
//                    .setServerPort(3000)
                    .setDeviceId("deviceId-test" + i)
                    .setOsName("android")
                    .setOsVersion("6.0")
                    .setClientVersion("2.0")
                    .setUserId("user-" + i)
                    .setTags("tag-" + i)
                    .setSessionStorageDir(cacheDir + i)
                    .setLogger(new DefaultLogger())
                    .setLogEnabled(true)
                    .setEnableHttpProxy(true)
                    .setClientListener(listener)
                    .create();
            client.start();
            Thread.sleep(sleep);
//            Future<HttpResponse> su = client.sendHttp(HttpRequest.buildPost("http://172.16.0.154:8080/api/admin/push.json?userId=user-0&content=test111111111111"));
//            System.out.println("aaaaaaaa========" +su);
        }
    }

    public static class L implements ClientListener {
        private final ScheduledExecutorService scheduledExecutor;
        boolean flag = true;

        public L(ScheduledExecutorService scheduledExecutor) {
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
//            while (true){
//                client.healthCheck();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

                scheduledExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        int s = 0;
                        while (true) {
                        boolean b = client.healthCheck();
                            if(!b){
                                client.start();
                                System.out.println("fastConnect========");
                            }
                            System.out.println("发送心跳========" + s+b);
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            s++;
                        }
                    }
                }, 10, 10, TimeUnit.SECONDS);

//            client.push(PushContext.build("test88888888888888888888888888888888888888888888888888888888888888888"));

        }

        @Override
        public void onReceivePush(Client client, byte[] content, int messageId) {
            if (messageId > 0) client.ack(messageId);
            try {
                String s = new String(content, "UTF-8");
                System.out.println("sssssssss========"+s+"messageId="+messageId);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Future<HttpResponse> su = client.sendHttp(HttpRequest.buildPost("http://42.159.117.106:8080/api/admin/push.json?userId=user-1&content=test111111111111"));
//            Future<Boolean> su = client.push(PushContext.build("test88888888888888888888888888888888888888888888888888888888888888888"));
            System.out.println("aaaaaaaa========" +su);
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

}