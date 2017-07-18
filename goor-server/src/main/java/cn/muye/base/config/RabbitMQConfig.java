package cn.muye.base.config;

import cn.mrobot.bean.constant.TopicConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    //从服务端发送到客户端命令队列
    //以下为服务器一对多发送
    @Bean
    public Queue fanoutCommon() {
        return new Queue(TopicConstants.FANOUT_SERVER_COMMAND,false,false,true);
    }

    @Bean
    public FanoutExchange fanoutCommandExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_COMMAND_EXCHANGE,false,true);
    }

    @Bean
    public FanoutExchange fanoutResourceExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_RESOURCE_EXCHANGE,false,true);
    }

    @Bean
    public FanoutExchange fanoutClientExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_CLIENT_EXCHANGE,false,true);
    }

    @Bean
    public Binding bindingFanoutCommandExchange(Queue fanoutCommon, FanoutExchange fanoutCommandExchange) {
        return BindingBuilder.bind(fanoutCommon).to(fanoutCommandExchange);
    }

    @Bean
    public Binding bindingFanoutResourceExchange(Queue fanoutCommon, FanoutExchange fanoutResourceExchange) {
        return BindingBuilder.bind(fanoutCommon).to(fanoutResourceExchange);
    }

    @Bean
    public Binding bindingFanoutClientExchange(Queue fanoutCommon, FanoutExchange fanoutClientExchange) {
        return BindingBuilder.bind(fanoutCommon).to(fanoutClientExchange);
    }

    //以下为服务器一对一发送
    @Bean
    public Queue topicCommon() {
        return new Queue(TopicConstants.TOPIC_SERVER_COMMAND, false, false, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TopicConstants.TOPIC_EXCHANGE, false, true);
    }

    @Bean
    public Binding bindingTopicExchangeMessages(Queue topicCommon, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicCommon).to(topicExchange).with(TopicConstants.TOPIC_SERVER_ROUTING_KEY);
    }

    //以下为goor客户端上报到goor-server端上报队列
    @Bean
    public Queue directCommandReport() {
        return new Queue(TopicConstants.DIRECT_COMMAND_REPORT,false,false,true);
    }

    @Bean
    public Queue directCommandReportAndReceive() {
        return new Queue(TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE,false,false,true);
    }

    @Bean
    public Queue directAppSub() {
        return new Queue(TopicConstants.DIRECT_APP_SUB,false,false,true);
    }

    @Bean
    public Queue directAppPub() {
        return new Queue(TopicConstants.DIRECT_APP_PUB,false,false,true);
    }

    @Bean
    public Queue directAgentSub() {
        return new Queue(TopicConstants.DIRECT_AGENT_SUB,false,false,true);
    }

    @Bean
    public Queue directAgentPub() {
        return new Queue(TopicConstants.DIRECT_AGENT_PUB,false,false,true);
    }

    @Bean
    public Queue directCurrentPosition() {
        return new Queue(TopicConstants.DIRECT_CURRENT_POSE,false,false,true);
    }

    @Bean
    public Queue directRobotAutoRegister() {
        return new Queue(TopicConstants.DIRECT_COMMAND_ROBOT_INFO,false,false,true);
    }

    @Bean
    public Queue directX86MissionQueueResponse() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_QUEUE_RESPONSE,false,false,true);
    }

    @Bean
    public Queue directX86MissionStateResponse() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_STATE_RESPONSE,false,false,true);
    }

    @Bean
    public Queue directX86MissionEvent() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_EVENT,false,false,true);
    }

    @Bean
    public Queue directX86MissionReceive() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_RECEIVE,false,false,true);
    }
}
