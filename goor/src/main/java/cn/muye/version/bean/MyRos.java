package cn.muye.version.bean;

import cn.muye.version.callback.MyServiceCallback;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.callback.CallServiceCallback;
import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.handler.RosHandler;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.util.Base64Utils;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import javax.websocket.*;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Created by chay on 2018/1/11.
 */
@ClientEndpoint
public class MyRos extends Ros{
    private Logger logger = Logger.getLogger(MyRos.class);

    private final HashMap<String, ArrayList<TopicCallback>> topicCallbacks = new HashMap<>();
    private final HashMap<String, ServiceCallback> serviceCallbacks  = new HashMap<>();
    private final HashMap<String, CallServiceCallback> callServiceCallbacks  = new HashMap<>();
    private Session session;
    private final ArrayList<RosHandler> handlers = new ArrayList<>();

    public MyRos() {
        super();
    }

    public MyRos(String hostname) {
        super(hostname);
    }

    @Override
    @OnMessage
    public void onMessage(String message) {
        try {
            JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
            String op = jsonObject.getString("op");
            if(op.equals("png")) {
                String data = jsonObject.getString("data");
                byte[] bytes = Base64Utils.decode(data.getBytes());
                Raster imageData = ImageIO.read(new ByteArrayInputStream(bytes)).getRaster();
                int[] rawData = null;
                rawData = imageData.getPixels(0, 0, imageData.getWidth(), imageData.getHeight(), (int[])rawData);
                StringBuffer buffer = new StringBuffer();

                for(int i = 0; i < rawData.length; ++i) {
                    buffer.append(Character.toString((char)rawData[i]));
                }

                JsonObject newJsonObject = Json.createReader(new StringReader(buffer.toString())).readObject();
                this.myHandleMessage(newJsonObject);
            } else {
                this.myHandleMessage(jsonObject);
            }
        } catch (IOException | JsonParsingException | NullPointerException var10) {
            System.err.println("[WARN]: Invalid incoming rosbridge protocol: " + message);
        }

    }

    /**
     * 在原有的基础上对异常进行了处理
     * @param jsonObject
     */
    private void myHandleMessage(JsonObject jsonObject) {
        String op = jsonObject.getString("op");
        String id;
        if(op.equals("publish")) {
            id = jsonObject.getString("topic");
            ArrayList<TopicCallback> callbacks = (ArrayList)this.topicCallbacks.get(id);
            if(callbacks != null) {
                Message msg = new Message(jsonObject.getJsonObject("msg"));
                Iterator i$ = callbacks.iterator();

                while(i$.hasNext()) {
                    TopicCallback cb = (TopicCallback)i$.next();
                    cb.handleMessage(msg);
                }
            }
        } else {
            JsonObject args;
            if(op.equals("service_response")) {
                id = jsonObject.getString("id");
                ServiceCallback cb = (ServiceCallback) this.serviceCallbacks.get(id);
                if(cb != null) {
                    boolean success = jsonObject.containsKey("result")?jsonObject.getBoolean("result"):true;
                    if(success) {
                        args = jsonObject.getJsonObject("values");
                        ServiceResponse response = new ServiceResponse(args, success);
                        cb.handleServiceResponse(response);
                    }
                    else {
                        String msg = jsonObject.getString("values");
                        //接口对象其实是建立了一个匿名内部类
                        MyServiceCallback jCb = new MyServiceCallback() {
                            @Override
                            public void handleServiceResponse(ServiceResponse serviceResponse) {
                                cb.handleServiceResponse(serviceResponse);
                            }

                            @Override
                            public void handleFailed(String msg) {
                                logger.error(msg);
                            }
                        };
                        jCb.handleFailed(msg);
                    }

                }
            } else if(op.equals("call_service")) {
                id = jsonObject.getString("id");
                String service = jsonObject.getString("service");
                CallServiceCallback cb = (CallServiceCallback)this.callServiceCallbacks.get(service);
                if(cb != null) {
                    args = jsonObject.getJsonObject("args");
                    ServiceRequest request = new ServiceRequest(args);
                    request.setId(id);
                    cb.handleServiceCall(request);
                }
            } else {
                System.err.println("[WARN]: Unrecognized op code: " + jsonObject.toString());
            }
        }

    }

    public HashMap<String, ArrayList<TopicCallback>> getTopicCallbacks() {
        return topicCallbacks;
    }

    public HashMap<String, ServiceCallback> getServiceCallbacks() {
        return serviceCallbacks;
    }

    public HashMap<String, CallServiceCallback> getCallServiceCallbacks() {
        return callServiceCallbacks;
    }

    /**
     * 在原有的基础上对websocket同步消息发送进行替换，用异步消息发送
     * @param jsonObject
     * @return
     */
    @Override
    public boolean send(JsonObject jsonObject) {
        if(this.isConnected()) {
            try {
                this.session.getBasicRemote().sendText(jsonObject.toString());
//                this.session.getAsyncRemote().sendText(jsonObject.toString());
                return true;
            } catch (Exception var3) {
                System.err.println("[ERROR]: Could not send message: " + var3.getMessage());
            }
        }

        return false;
    }

    /**
     * 以下复写的方法与父类完全相同，只是为了使用父类的私有对象，必须引用一遍
     * **/
    @Override
    public void registerTopicCallback(String topic, TopicCallback cb) {
        if(!this.topicCallbacks.containsKey(topic)) {
            this.topicCallbacks.put(topic, new ArrayList());
        }

        ((ArrayList)this.topicCallbacks.get(topic)).add(cb);
    }

    @Override
    public void deregisterTopicCallback(String topic, TopicCallback cb) {
        if(this.topicCallbacks.containsKey(topic)) {
            ArrayList<TopicCallback> callbacks = (ArrayList)this.topicCallbacks.get(topic);
            if(callbacks.contains(cb)) {
                callbacks.remove(cb);
            }

            if(callbacks.size() == 0) {
                this.topicCallbacks.remove(topic);
            }
        }

    }

    @Override
    public void registerServiceCallback(String serviceCallId, ServiceCallback cb) {
        this.serviceCallbacks.put(serviceCallId, cb);
    }

    @Override
    public void registerCallServiceCallback(String serviceName, CallServiceCallback cb) {
        this.callServiceCallbacks.put(serviceName, cb);
    }

    @Override
    public void deregisterCallServiceCallback(String serviceName) {
        this.callServiceCallbacks.remove(serviceName);
    }
    @Override
    public void addRosHandler(RosHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public boolean disconnect() {
        if(this.isConnected()) {
            try {
                this.session.close();
                return true;
            } catch (IOException var2) {
                System.err.println("[ERROR]: Could not disconnect: " + var2.getMessage());
            }
        }

        return false;
    }

    @Override
    public boolean isConnected() {
        return this.session != null && this.session.isOpen();
    }

    @Override
    public void onOpen(Session session) {
        this.session = session;
        Iterator i$ = this.handlers.iterator();

        while(i$.hasNext()) {
            RosHandler handler = (RosHandler)i$.next();
            handler.handleConnection(session);
        }

    }

    @Override
    public void onClose(Session session) {
        this.session = null;
        Iterator i$ = this.handlers.iterator();

        while(i$.hasNext()) {
            RosHandler handler = (RosHandler)i$.next();
            handler.handleDisconnection(session);
        }

    }

    @Override
    public void onError(Session session, Throwable t) {
        Iterator i$ = this.handlers.iterator();

        while(i$.hasNext()) {
            RosHandler handler = (RosHandler)i$.next();
            handler.handleError(session, t);
        }

    }
}
