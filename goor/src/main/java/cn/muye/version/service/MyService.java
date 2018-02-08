package cn.muye.version.service;

import cn.muye.version.bean.MyRos;
import edu.wpi.rail.jrosbridge.JRosbridge;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by enva on 2017/12/8.
 */
public class MyService extends Service {

    //TODO 如果RosConfig里面用MyRos，用以下构造函数
    MyRos ros;

    public MyService(Ros ros, String name, String type) {
        super(ros, name, type);
        this.ros = (MyRos) ros;
    }

    @Override
    public MyRos getRos() {
        return ros;
    }

    //如果RosConfig里面用Ros，用以下构造函数
    /*public MyService(Ros ros, String name, String type) {
        super(ros, name, type);
    }*/

    @Override
    public void callService(ServiceRequest request, ServiceCallback cb) {
        // construct the unique ID
        String callServceId = "call_service:" + this.getName() + ":"
                + this.getRos().nextId();

        // register the callback function
        this.getRos().registerServiceCallback(callServceId, cb);

        // build and send the rosbridge call
        JsonObject call = Json.createObjectBuilder()
                .add(JRosbridge.FIELD_OP, JRosbridge.OP_CODE_CALL_SERVICE)
                .add(JRosbridge.FIELD_ID, callServceId)
                .add(JRosbridge.FIELD_TYPE, this.getType())
                .add(JRosbridge.FIELD_SERVICE, this.getName())
                .add(JRosbridge.FIELD_ARGS, request.toJsonObject()).build();
        this.getRos().send(call);
    }

    @Override
    public synchronized ServiceResponse callServiceAndWait(
            ServiceRequest request) {
        ABlockingCallback cb = new ABlockingCallback(this);
        this.callService(request, cb);
//        while (cb.getResponse() == null) {
            try {
                this.wait(3);
            } catch (InterruptedException e) {
                // continue on
            }
//        }
        return cb.getResponse();
    }

    private class ABlockingCallback implements ServiceCallback {
        private ServiceResponse response;
        private Service service;
        public ABlockingCallback(Service service) {
            this.response = null;
            this.service = service;
        }

        @Override
        public void handleServiceResponse(ServiceResponse response) {
            this.response = response;
            synchronized (this.service) {
                this.service.notifyAll();
            }
        }

        public ServiceResponse getResponse() {
            return this.response;
        }
    }

}
