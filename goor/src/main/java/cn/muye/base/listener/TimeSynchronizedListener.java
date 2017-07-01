package cn.muye.base.listener;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Info: X86与云端通信同步命令监听
 * User: Gary.zhang@mrobot.cn
 * Date: 2017-07-01
 * Time: 11:34
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */
@Component
@RabbitListener(queues = "time.synchronized.down")
public class TimeSynchronizedListener {

    @Autowired
    private Ros ros;

    public int processTimeSynchronized(int currentTime){
        if(currentTime != 999){
            //调用rosservice进行时间同步
            Date date = new Date();
            long synchronizedTime = date.getTime()/1000;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(sdf.format(synchronizedTime));

            Service addTwoInts = new Service(ros, "/sync_system_time", "sync_system_time/UpdateTime");

            String jsonString = "{\"sync_time\": " + synchronizedTime + "}";
            System.out.println("********************" + jsonString);
            ServiceRequest request = new ServiceRequest(jsonString, "sync_system_time/UpdateTime");
            ServiceResponse response = addTwoInts.callServiceAndWait(request);
            System.out.println(response.toString());
        }
        return 0;
    }
}
