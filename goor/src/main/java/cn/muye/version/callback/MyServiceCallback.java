package cn.muye.version.callback;

import edu.wpi.rail.jrosbridge.callback.ServiceCallback;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

/**
 * @author Created by xiaoni on 2018/1/10.
 */
public interface MyServiceCallback extends ServiceCallback {

    @Override
    void handleServiceResponse(ServiceResponse serviceResponse);

    abstract void handleFailed(String msg);
}
