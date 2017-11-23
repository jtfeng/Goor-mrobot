package cn.muye.base.config;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.RabbitmqUtil;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    /**
     * 命令
     */
    @Value(TopicConstants.FANOUT_COMMAND)
    private String fanoutCommandSN;

    @Value(TopicConstants.TOPIC_COMMAND)
    private String topicCommandSN;

    @Value(TopicConstants.TOPIC_RECEIVE_COMMAND)
    private String topicCommandAndReceiveSN;

    /**
     *资源
     */
    @Value(TopicConstants.FANOUT_RESOURCE)
    private String fanoutResourceSN;

    @Value(TopicConstants.TOPIC_RESOURCE)
    private String topicResourceSN;

    @Value(TopicConstants.TOPIC_RECEIVE_RESOURCE)
    private String topicResourceAndReceiveSN;

    /**
     *只到x86机器人上,不处理ros通信之类，只和x86客户端通信
     */
    @Value(TopicConstants.FANOUT_CLIENT)
    private String fanoutClientSN;

    @Value(TopicConstants.TOPIC_CLIENT)
    private String topicClientSN;

    @Value(TopicConstants.TOPIC_RECEIVE_CLIENT)
    private String topicClientAndReceiveSN;


    //从服务端发送到客户端命令队列
    //以下为服务器一对多发送
    @Bean
    public Queue fanoutCommand() {
        return new Queue(fanoutCommandSN,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue fanoutResource() {
        return new Queue(fanoutResourceSN,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue fanoutClient() {
        return new Queue(fanoutClientSN,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public FanoutExchange fanoutCommandExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_COMMAND_EXCHANGE,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public FanoutExchange fanoutResourceExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_RESOURCE_EXCHANGE,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public FanoutExchange fanoutClientExchange() {
        return new FanoutExchange(TopicConstants.FANOUT_CLIENT_EXCHANGE,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Binding bindingFanoutCommandExchange(Queue fanoutCommand, FanoutExchange fanoutCommandExchange) {
        return BindingBuilder.bind(fanoutCommand).to(fanoutCommandExchange);
    }

    @Bean
    public Binding bindingFanoutResourceExchange(Queue fanoutResource, FanoutExchange fanoutResourceExchange) {
        return BindingBuilder.bind(fanoutResource).to(fanoutResourceExchange);
    }

    @Bean
    public Binding bindingFanoutClientExchange(Queue fanoutClient, FanoutExchange fanoutClientExchange) {
        return BindingBuilder.bind(fanoutClient).to(fanoutClientExchange);
    }

    //以下为服务器一对一发送
    @Bean
    public Queue topicCommand() {
        return new Queue(topicCommandSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue topicCommandAndReceive() {
        return new Queue(topicCommandAndReceiveSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue topicResource() {
        return new Queue(topicResourceSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue topicResourceAndReceive() {
        return new Queue(topicResourceAndReceiveSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue topicClient() {
        return new Queue(topicClientSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue topicClientAndReceive() {
        return new Queue(topicClientAndReceiveSN, false, false, true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TopicConstants.TOPIC_EXCHANGE, false, true);
    }

    @Bean
    public Binding bindingTopicCommandExchangeMessages(Queue topicCommand, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicCommand).to(topicExchange).with(topicCommandSN);
    }

    @Bean
    public Binding bindingTopicCommandExchangeReceiveMessages(Queue topicCommandAndReceive, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicCommandAndReceive).to(topicExchange).with(topicCommandAndReceiveSN);
    }

    @Bean
    public Binding bindingTopicResourceExchangeMessages(Queue topicResource, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicResource).to(topicExchange).with(topicResourceSN);
    }

    @Bean
    public Binding bindingTopicResourceExchangeReceiveMessages(Queue topicResourceAndReceive, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicResourceAndReceive).to(topicExchange).with(topicResourceAndReceiveSN);
    }

    @Bean
    public Binding bindingTopicClientExchangeMessages(Queue topicClient, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicClient).to(topicExchange).with(topicClientSN);
    }

    @Bean
    public Binding bindingTopicClientExchangeReceiveMessages(Queue topicClientAndReceive, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicClientAndReceive).to(topicExchange).with(topicClientAndReceiveSN);
    }

    //以下为goor客户端上报到goor-server端上报队列
    @Bean
    public Queue directCommandReport() {
        return new Queue(TopicConstants.DIRECT_COMMAND_REPORT,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directRobotAutoRegister() {
        return new Queue(TopicConstants.DIRECT_COMMAND_ROBOT_INFO,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directCommandReportAndReceive() {
        return new Queue(TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directAppSub() {
        return new Queue(TopicConstants.DIRECT_APP_SUB,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directAppPub() {
        return new Queue(TopicConstants.DIRECT_APP_PUB,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directAgentSub() {
        return new Queue(TopicConstants.DIRECT_AGENT_SUB,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directAgentPub() {
        return new Queue(TopicConstants.DIRECT_AGENT_PUB,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directCurrentPosition() {
        return new Queue(TopicConstants.DIRECT_CURRENT_POSE,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directPower() {
        return new Queue(TopicConstants.DIRECT_APP_SUB_POWER,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86MissionQueueResponse() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_QUEUE_RESPONSE,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86MissionStateResponse() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_STATE_RESPONSE,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86MissionEvent() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_EVENT,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86MissionAlert() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_ALERT,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86MissionReceive() {
        return new Queue(TopicConstants.DIRECT_X86_MISSION_RECEIVE,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directStateCollector() {
        return new Queue(TopicConstants.DIRECT_STATE_COLLECTOR,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86ElevatorLock() {
        return new Queue(TopicConstants.DIRECT_X86_ELEVATOR_LOCK,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }

    @Bean
    public Queue directX86RoadPathLock() {
        return new Queue(TopicConstants.DIRECT_X86_ROADPATH_LOCK,false,false,true, RabbitmqUtil.getRabbitMQArguments());
    }
}
