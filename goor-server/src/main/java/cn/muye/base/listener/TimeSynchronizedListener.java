package cn.muye.base.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Info: X86与云端时间同步
 * User: Gary.zhang@mrobot.cn
 * Date: 2017-07-01
 * Time: 10:43
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */
@Component
@RabbitListener(queues = "time.synchronized.up")
public class TimeSynchronizedListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void processTimeSynchronizedMessage(long currentTime) {
        //监听上行X86时间同步消息，获得X86时间，与服务器时间比较，如果差值大于10s（默认），进行时间同步，否则不处理
        Date date = new Date();
        if(currentTime- date.getTime() < 10){
            return;
        } else {
            //发送带响应同步消息，获得10次时间平均延迟
            int sum = 0;

            for (int i = 0; i < 10; i++) {
                long startTime = System.currentTimeMillis();
                int result = (int) rabbitTemplate.convertSendAndReceive("time.synchronized.down", 9999);//后期带上机器编码进行区分
                long endTime = System.currentTimeMillis();
                sum += (endTime - startTime);
            }
            int avg = sum / 10;
            //给指定X86发送时间同步消息
            rabbitTemplate.convertAndSend("time.synchronize.down",new Date().getTime()+avg);
        }








    }
}
