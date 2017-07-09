package cn.muye.base.listener;


import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.MessageInfo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Created by njxiaohan on 2017/5/31.
 */
@Component
//@RabbitListener(queues = "enva", containerFactory="myHandlerMethodFactory")
//@RabbitListener(queues ="direct.commands")
//@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "enva", autoDelete = "true") , exchange = @Exchange(value = "exch", autoDelete = "true") , key = "key") )
public class DemoConsumer {
//    @Value("${local.robot.SN}")
//    private String localRobotSN;
//
//    @Value("${local.robot.SN1}")
//    private String localRobotSN1;
//    @RabbitListener(queues ="enva")
//    @RabbitHandler
//    @RabbitListener(queues ="direct.commands")
//    @RabbitListener(queues = "enva", containerFactory="rabbitListenerContainerFactory")
    public String processMessage(@Payload String request) {
        if(request != null){
            System.out.println("message========="+request);
                return request;
        }
        return request;
    }

//    @RabbitListener(queues ="enva_send_only")
//    @RabbitListener(queues ="direct.command")
//    @RabbitListener(queues = "enva_send_only", containerFactory="rabbitListenerContainerFactory")
    public String processMessageSendOnly(@Payload String request) {
        if(request != null){
            System.out.println("processMessageSendOnly========="+request);
        }
        return request;
    }

    //    @RabbitListener(queues ="enva_send_only")
//    @RabbitListener(queues ="direct.resource")
//    @RabbitListener(queues = "#{'${local.robot.SN1}'}" )
//    @RabbitListener(queues = "enva_send_only", containerFactory="rabbitListenerContainerFactory")
    public void processMessageResource(@Payload MessageInfo request) {
        if(request != null){
            System.out.println("processMessageResource========="+request);
        }
//        return request;
    }

    //    @RabbitListener(queues ="enva_send_only")
//    @RabbitListener(queues = "#{rabbitConfiguration.FANOUT_QUEUE}")
//    @RabbitListener(queues = "#{'${local.robot.SN}'}" )
//    @RabbitListener(queues ="fanout.command")
//    @RabbitListener(queues = "enva_send_only", containerFactory="rabbitListenerContainerFactory")
    public  void  processfanoutMessageCommand(@Payload MessageInfo request) {
        if(request != null){
            System.out.println("processfanoutMessagecommand========="+request);
        }
//        return request;
    }

    @RabbitListener(queues = TopicConstants.TOPIC_COMMAND )
    public  void  processTopicMessageCommand(@Payload String request) {
        if(request != null){
            System.out.println("processTopicMessagecommand========="+request);
        }
    }

    @RabbitListener(queues = TopicConstants.FANOUT_COMMAND )
    public  void  processFanoutMessageCommand(@Payload String request) {
        if(request != null){
            System.out.println("processFanoutMessagecommand========="+request);
        }
    }

//    @RabbitListener(queues = TopicConstants.TOPIC_COMMAND )
    public  void  processTopicMessageCommand1(@Payload MessageInfo request) {
        if(request != null){
            System.out.println("processTopicMessagecommand1111111========="+request);
        }
    }

    //    @RabbitListener(queues ="enva_send_only")
//    @RabbitListener(queues ="fanout.resource")
////    @RabbitListener(queues = "enva_send_only", containerFactory="rabbitListenerContainerFactory")
//    public String processfanoutMessageResource(@Payload String request) {
//        if(request != null){
//            System.out.println("processfanoutMessageResource========="+request);
//        }
//        return request;
//    }


}