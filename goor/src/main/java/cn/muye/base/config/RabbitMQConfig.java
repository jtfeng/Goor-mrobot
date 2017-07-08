package cn.muye.base.config;

import cn.mrobot.bean.constant.TopicConstants;
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


    //从服务端发送到客户端命令队列
    //以下为服务器一对多发送
    @Bean
    public Queue fanoutCommand() {
        return new Queue(fanoutCommandSN,false,false,true);
    }

    @Bean
    public Queue fanoutResource() {
        return new Queue(fanoutResourceSN,false,false,true);
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
    public Binding bindingFanoutCommandExchange(Queue fanoutCommand, FanoutExchange fanoutCommandExchange) {
        return BindingBuilder.bind(fanoutCommand).to(fanoutCommandExchange);
    }

    @Bean
    public Binding bindingFanoutResourceExchange(Queue fanoutResource, FanoutExchange fanoutResourceExchange) {
        return BindingBuilder.bind(fanoutResource).to(fanoutResourceExchange);
    }

    //以下为服务器一对一发送
    @Bean
    public Queue topicCommand() {
        return new Queue(topicCommandSN, false, false, true);
    }

    @Bean
    public Queue topicCommandAndReceive() {
        return new Queue(topicCommandAndReceiveSN, false, false, true);
    }

    @Bean
    public Queue topicResource() {
        return new Queue(topicResourceSN, false, false, true);
    }

    @Bean
    public Queue topicResourceAndReceive() {
        return new Queue(topicResourceAndReceiveSN, false, false, true);
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
    public Binding bindingTopicExchangeMessages(Queue topicResource, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicResource).to(topicExchange).with(topicResourceSN);
    }

    @Bean
    public Binding bindingTopicExchangeReceiveMessages(Queue topicResourceAndReceive, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicResourceAndReceive).to(topicExchange).with(topicResourceAndReceiveSN);
    }

    //以下为goor客户端上报到goor-server端上报队列
    @Bean
    public Queue directCommandReport() {
        return new Queue(TopicConstants.DIRECT_COMMAND_REPORT,false,false,true);
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

}
